package com.example.arate.lectures.repository;

import com.example.arate.lectures.entity.LectureEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureEvaluationRepository extends JpaRepository<LectureEvaluation, Long> {
    List<LectureEvaluation> findByLectureId(Long lectureId);
    
    List<LectureEvaluation> findByUserId(Long userId);
    
    Optional<LectureEvaluation> findByLectureIdAndUserId(Long lectureId, Long userId);

    boolean existsByLectureIdAndUserId(Long lectureId, Long userId);
    
    long countByUserId(Long userId);

    long countByLectureId(Long lectureId);

    // 최신 강의평 조회 (대시보드용)
    List<LectureEvaluation> findTop20ByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(AVG(e.deliveryScore), 0) FROM LectureEvaluation e WHERE e.lectureId = :lectureId")
    double getAverageDeliveryScore(@Param("lectureId") Long lectureId);

    @Query("SELECT COALESCE(AVG(e.expertiseScore), 0) FROM LectureEvaluation e WHERE e.lectureId = :lectureId")
    double getAverageExpertiseScore(@Param("lectureId") Long lectureId);

    @Query("SELECT COALESCE(AVG(e.generosityScore), 0) FROM LectureEvaluation e WHERE e.lectureId = :lectureId")
    double getAverageGenerosityScore(@Param("lectureId") Long lectureId);

    @Query("SELECT COALESCE(AVG(e.effectivenessScore), 0) FROM LectureEvaluation e WHERE e.lectureId = :lectureId")
    double getAverageEffectivenessScore(@Param("lectureId") Long lectureId);

    @Query("SELECT COALESCE(AVG(e.characterScore), 0) FROM LectureEvaluation e WHERE e.lectureId = :lectureId")
    double getAverageCharacterScore(@Param("lectureId") Long lectureId);

    @Query("SELECT COALESCE(AVG(e.difficultyScore), 0) FROM LectureEvaluation e WHERE e.lectureId = :lectureId")
    double getAverageDifficultyScore(@Param("lectureId") Long lectureId);
} 