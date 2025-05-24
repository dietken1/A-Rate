package com.example.arate.enrollments.repository;

import com.example.arate.enrollments.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    List<Enrollment> findByStudentId(Long studentId);
    
    List<Enrollment> findByLectureId(Long lectureId);
    
    Optional<Enrollment> findByStudentIdAndLectureId(Long studentId, Long lectureId);
    
    boolean existsByStudentIdAndLectureId(Long studentId, Long lectureId);
    
    boolean existsByStudentIdAndLectureIdAndIsCertifiedTrue(Long studentId, Long lectureId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.studentId = :studentId AND e.isCertified = true")
    long countCertifiedEnrollmentsByStudentId(@Param("studentId") Long studentId);
    
    Page<Enrollment> findByIsCertified(Boolean isCertified, Pageable pageable);
} 