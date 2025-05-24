package com.example.arate.lecture.service;

import com.example.arate.lecture.dto.LectureDetailResponse;
import com.example.arate.lecture.dto.LectureResponse;
import com.example.arate.lecture.entity.Lecture;
import com.example.arate.lecture.exception.LectureException;
import com.example.arate.lecture.repository.LectureEvaluationRepository;
import com.example.arate.lecture.repository.LectureRepository;
import com.example.arate.users.entity.User;
import com.example.arate.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LectureEvaluationService lectureEvaluationService;
    private final LectureEvaluationRepository lectureEvaluationRepository;
    private final UserRepository userRepository;

    public Page<LectureResponse> getLectures(String department, Long professorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Lecture> lectures = lectureRepository.findAllByDepartmentAndProfessorId(department, professorId, pageable);
        
        return lectures.map(lecture -> {
            User professor = userRepository.findById(lecture.getProfessorId())
                    .orElseThrow(() -> new LectureException.LectureNotFoundException());
            
            return new LectureResponse(
                lecture.getId(),
                lecture.getTitle(),
                professor.getName(),
                lecture.getDepartment(),
                (int)lectureEvaluationRepository.countByLectureId(lecture.getId()),
                lectureEvaluationRepository.getAverageDeliveryScore(lecture.getId())
            );
        });
    }

    public LectureDetailResponse getLectureDetail(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(LectureException.LectureNotFoundException::new);

        User professor = userRepository.findById(lecture.getProfessorId())
                .orElseThrow(() -> new LectureException.LectureNotFoundException());

        return new LectureDetailResponse(
            lecture.getId(),
            lecture.getTitle(),
            new LectureDetailResponse.ProfessorInfo(
                lecture.getProfessorId(),
                professor.getName(),
                lecture.getDepartment()
            ),
            lecture.getDepartment(),
            lectureEvaluationService.getEvaluationsByLectureId(lectureId),
            lectureEvaluationService.getAverageScores(lectureId)
        );
    }
}