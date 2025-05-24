package com.example.arate.lectures.service;

import com.example.arate.enrollments.service.EnrollmentService;
import com.example.arate.lectures.dto.LectureDetailResponse;
import com.example.arate.lectures.dto.LectureResponse;
import com.example.arate.lectures.entity.Lecture;
import com.example.arate.lectures.exception.LectureException;
import com.example.arate.lectures.repository.LectureEvaluationRepository;
import com.example.arate.lectures.repository.LectureRepository;
import com.example.arate.users.entity.User;
import com.example.arate.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LectureEvaluationService lectureEvaluationService;
    private final LectureEvaluationRepository lectureEvaluationRepository;
    private final UserRepository userRepository;
    private final EnrollmentService enrollmentService;

    public Page<LectureResponse> getLectures(String department, Long professorId, int page, int size) {
        return getLectures(null, department, null, professorId, page, size);
    }

    /**
     * 통합 강의 검색: 키워드 + 필터링으로 강의 검색
     * 
     * @param keyword 검색 키워드 (교수명과 과목명을 동시 검색, null이면 전체)
     * @param department 학과 ("all"이면 필터링 안함)
     * @param courseType 학수구분 ("all"이면 필터링 안함)
     * @param professorId 교수 ID (기존 호환성용, null이면 필터링 안함)
     * @param page 페이지 번호
     * @param size 페이지 크기
     */
    public Page<LectureResponse> getLectures(String keyword, String department, String courseType, 
                                           Long professorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        // "all" 값을 null로 변환
        String finalDepartment = "all".equals(department) ? null : department;
        String finalCourseType = "all".equals(courseType) ? null : courseType;
        
        Page<Lecture> lectures = lectureRepository.findLecturesWithFilters(
            keyword, finalDepartment, finalCourseType, professorId, pageable
        );
        
        return lectures.map(lecture -> {
            User professor = userRepository.findById(lecture.getProfessorId())
                    .orElseThrow(() -> new LectureException.LectureNotFoundException());
            
            return new LectureResponse(
                lecture.getId(),
                lecture.getTitle(),
                professor.getName(),
                lecture.getDepartment(),
                Math.toIntExact(lectureEvaluationRepository.countByLectureId(lecture.getId())),
                lectureEvaluationRepository.getAverageDeliveryScore(lecture.getId())
            );
        });
    }

    public LectureDetailResponse getLectureDetail(Long lectureId) {
        return getLectureDetailWithUser(lectureId, null);
    }

    public LectureDetailResponse getLectureDetailWithUser(Long lectureId, Long userId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(LectureException.LectureNotFoundException::new);

        User professor = userRepository.findById(lecture.getProfessorId())
                .orElseThrow(() -> new LectureException.LectureNotFoundException());

        boolean hasWrittenEvaluation = userId != null && 
                enrollmentService.canViewEvaluations(userId);

        return new LectureDetailResponse(
            lecture.getId(),
            lecture.getTitle(),
            new LectureDetailResponse.ProfessorInfo(
                lecture.getProfessorId(),
                professor.getName(),
                lecture.getDepartment()
            ),
            lecture.getDepartment(),
            lectureEvaluationService.getEvaluationsByLectureId(lectureId, hasWrittenEvaluation),
            lectureEvaluationService.getAverageScores(lectureId)
        );
    }

    /**
     * 평점이 가장 높은 강의들을 조회합니다.
     * 최소 3개 이상의 강의평이 있는 강의만 포함됩니다.
     */
    public List<LectureResponse> getTopRatedLectures(int limit) {
        List<Lecture> allLectures = lectureRepository.findAll();
        
        return allLectures.stream()
                .filter(lecture -> lectureEvaluationRepository.countByLectureId(lecture.getId()) >= 3)
                .map(lecture -> {
                    User professor = userRepository.findById(lecture.getProfessorId())
                            .orElse(null);
                    if (professor == null) return null;
                    
                    return new LectureResponse(
                        lecture.getId(),
                        lecture.getTitle(),
                        professor.getName(),
                        lecture.getDepartment(),
                        Math.toIntExact(lectureEvaluationRepository.countByLectureId(lecture.getId())),
                        lectureEvaluationRepository.getAverageDeliveryScore(lecture.getId())
                    );
                })
                .filter(response -> response != null && response.getAverageScore() > 0)
                .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 평점이 가장 낮은 강의들을 조회합니다.
     * 최소 3개 이상의 강의평이 있는 강의만 포함됩니다.
     */
    public List<LectureResponse> getBottomRatedLectures(int limit) {
        List<Lecture> allLectures = lectureRepository.findAll();
        
        return allLectures.stream()
                .filter(lecture -> lectureEvaluationRepository.countByLectureId(lecture.getId()) >= 3)
                .map(lecture -> {
                    User professor = userRepository.findById(lecture.getProfessorId())
                            .orElse(null);
                    if (professor == null) return null;
                    
                    return new LectureResponse(
                        lecture.getId(),
                        lecture.getTitle(),
                        professor.getName(),
                        lecture.getDepartment(),
                        Math.toIntExact(lectureEvaluationRepository.countByLectureId(lecture.getId())),
                        lectureEvaluationRepository.getAverageDeliveryScore(lecture.getId())
                    );
                })
                .filter(response -> response != null && response.getAverageScore() > 0)
                .sorted((a, b) -> Double.compare(a.getAverageScore(), b.getAverageScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}