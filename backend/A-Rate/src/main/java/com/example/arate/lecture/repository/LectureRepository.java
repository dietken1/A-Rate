package com.example.arate.lecture.repository;

import com.example.arate.lecture.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    @Query("SELECT l FROM Lecture l WHERE " +
           "(:department IS NULL OR l.department = :department) AND " +
           "(:professorId IS NULL OR l.professorId = :professorId)")
    Page<Lecture> findAllByDepartmentAndProfessorId(
            @Param("department") String department,
            @Param("professorId") Long professorId,
            Pageable pageable
    );
} 