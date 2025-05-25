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
    
    List<Enrollment> findByStudent_Id(Long studentId);
    
    List<Enrollment> findByLecture_Id(Long lectureId);
    
    Optional<Enrollment> findByStudent_IdAndLecture_Id(Long studentId, Long lectureId);
    
    boolean existsByStudent_IdAndLecture_Id(Long studentId, Long lectureId);
    
    boolean existsByStudent_IdAndLecture_IdAndIsCertifiedTrue(Long studentId, Long lectureId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId AND e.isCertified = true")
    long countCertifiedEnrollmentsByStudentId(@Param("studentId") Long studentId);
    
    Page<Enrollment> findByIsCertified(Boolean isCertified, Pageable pageable);
} 