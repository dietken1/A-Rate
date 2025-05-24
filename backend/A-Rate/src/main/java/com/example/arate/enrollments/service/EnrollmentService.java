package com.example.arate.enrollments.service;

import com.example.arate.enrollments.dto.CreateEnrollmentRequest;
import com.example.arate.enrollments.dto.EnrollmentResponse;
import com.example.arate.enrollments.dto.UpdateEnrollmentRequest;
import com.example.arate.enrollments.entity.Enrollment;
import com.example.arate.enrollments.exception.EnrollmentException;
import com.example.arate.enrollments.repository.EnrollmentRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {
    
    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final LectureEvaluationRepository lectureEvaluationRepository;
    
    /**
     * 학생이 강의 인증 요청
     */
    @Transactional
    public EnrollmentResponse createEnrollment(Long studentId, CreateEnrollmentRequest request) {
        // 강의 존재 여부 확인
        Lecture lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(LectureException.LectureNotFoundException::new);
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 이미 인증 요청한 경우 예외 발생
        if (enrollmentRepository.existsByStudent_IdAndLecture_Id(studentId, request.getLectureId())) {
            throw new EnrollmentException.DuplicateEnrollmentException();
        }
        
        // 인증 요청 생성
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setLecture(lecture);
        enrollment.setCertificationImage(request.getCertificationImage());
        enrollment.setGrade(request.getGrade());
        enrollment.setSemester(request.getSemester());
        enrollment.setIsCertified(false); // 기본적으로 인증되지 않은 상태
        enrollment.setCreatedAt(LocalDateTime.now());
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        
        return new EnrollmentResponse(
            savedEnrollment.getId(),
            savedEnrollment.getStudent().getId(),
            savedEnrollment.getLecture().getId(),
            lecture.getTitle(),
            savedEnrollment.getCertificationImage(),
            savedEnrollment.getGrade(),
            savedEnrollment.getIsCertified(),
            savedEnrollment.getSemester(),
            savedEnrollment.getCreatedAt(),
            savedEnrollment.getCertifiedAt()
        );
    }
    
    /**
     * 관리자가 인증 요청 상태 변경 (승인/거부)
     */
    @Transactional
    public EnrollmentResponse updateEnrollmentStatus(Long enrollmentId, UpdateEnrollmentRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentException.EnrollmentNotFoundException());
        
        if (request.getIsCertified() != null) {
            enrollment.setIsCertified(request.getIsCertified());
            if (request.getIsCertified()) {
                enrollment.setCertifiedAt(LocalDateTime.now());
            } else {
                enrollment.setCertifiedAt(null);
            }
        }
        
        if (request.getCertificationImage() != null) {
            enrollment.setCertificationImage(request.getCertificationImage());
        }
        
        if (request.getGrade() != null) {
            enrollment.setGrade(request.getGrade());
        }
        
        if (request.getSemester() != null) {
            enrollment.setSemester(request.getSemester());
        }
        
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        
        return new EnrollmentResponse(
            updatedEnrollment.getId(),
            updatedEnrollment.getStudent().getId(),
            updatedEnrollment.getLecture().getId(),
            updatedEnrollment.getLecture().getTitle(),
            updatedEnrollment.getCertificationImage(),
            updatedEnrollment.getGrade(),
            updatedEnrollment.getIsCertified(),
            updatedEnrollment.getSemester(),
            updatedEnrollment.getCreatedAt(),
            updatedEnrollment.getCertifiedAt()
        );
    }
    
    /**
     * 학생의 인증 요청 목록 조회
     */
    public List<EnrollmentResponse> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudent_Id(studentId).stream()
                .map(enrollment -> {
                    return new EnrollmentResponse(
                        enrollment.getId(),
                        enrollment.getStudent().getId(),
                        enrollment.getLecture().getId(),
                        enrollment.getLecture().getTitle(),
                        enrollment.getCertificationImage(),
                        enrollment.getGrade(),
                        enrollment.getIsCertified(),
                        enrollment.getSemester(),
                        enrollment.getCreatedAt(),
                        enrollment.getCertifiedAt()
                    );
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 관리자: 인증 대기 중인 요청 목록 조회
     */
    public Page<EnrollmentResponse> getPendingEnrollments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return enrollmentRepository.findByIsCertified(false, pageable)
                .map(enrollment -> {
                    return new EnrollmentResponse(
                        enrollment.getId(),
                        enrollment.getStudent().getId(),
                        enrollment.getLecture().getId(),
                        enrollment.getLecture().getTitle(),
                        enrollment.getCertificationImage(),
                        enrollment.getGrade(),
                        enrollment.getIsCertified(),
                        enrollment.getSemester(),
                        enrollment.getCreatedAt(),
                        enrollment.getCertifiedAt()
                    );
                });
    }
    
    /**
     * 인증 요청 삭제
     */
    @Transactional
    public void deleteEnrollment(Long enrollmentId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentException.EnrollmentNotFoundException());
        
        // 요청자와 소유자가 일치하는지 확인
        if (!enrollment.getStudent().getId().equals(studentId)) {
            throw new EnrollmentException.UnauthorizedEnrollmentException();
        }
        
        enrollmentRepository.delete(enrollment);
    }
    
    /**
     * 강의평 작성 자격 확인 (인증된 수강생인지)
     */
    public boolean canWriteEvaluation(Long studentId, Long lectureId) {
        return enrollmentRepository.existsByStudent_IdAndLecture_IdAndIsCertifiedTrue(studentId, lectureId);
    }
    
    /**
     * 사용자가 강의평을 볼 수 있는지 확인
     * 한 개 이상의 강의평을 작성했으면 true 반환
     */
    public boolean canViewEvaluations(Long userId) {
        return lectureEvaluationRepository.countByUser_Id(userId) > 0;
    }
} 