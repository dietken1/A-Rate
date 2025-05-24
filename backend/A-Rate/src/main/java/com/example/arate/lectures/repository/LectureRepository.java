package com.example.arate.lectures.repository;

import com.example.arate.lectures.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    /**
     * 통합 강의 검색: 키워드로 교수명과 과목명 동시 검색 + 학과 + 학수구분 필터링
     * "all" 값은 필터링에서 제외
     * keyword는 교수명과 과목명을 OR 조건으로 검색 (부분 검색)
     */
    @Query("SELECT l FROM Lecture l " +
           "JOIN User u ON l.professorId = u.id " +
           "WHERE " +
           "(:keyword IS NULL OR l.title LIKE %:keyword% OR u.name LIKE %:keyword%) AND " +
           "(:department IS NULL OR :department = 'all' OR l.department = :department) AND " +
           "(:courseType IS NULL OR :courseType = 'all' OR l.courseType = :courseType) AND " +
           "(:professorId IS NULL OR l.professorId = :professorId)")
    Page<Lecture> findLecturesWithFilters(
            @Param("keyword") String keyword,
            @Param("department") String department,
            @Param("courseType") String courseType,
            @Param("professorId") Long professorId,
            Pageable pageable
    );
} 