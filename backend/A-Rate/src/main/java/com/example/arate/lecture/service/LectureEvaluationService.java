package com.example.arate.lecture.service;

import com.example.arate.lecture.dto.*;
import com.example.arate.lecture.entity.LectureEvaluation;
import com.example.arate.lecture.exception.LectureException;
import com.example.arate.lecture.repository.LectureEvaluationRepository;
import com.example.arate.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureEvaluationService {
    private final LectureEvaluationRepository lectureEvaluationRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public EvaluationResponse createEvaluation(Long lectureId, Long userId, CreateEvaluationRequest request) {
        // 강의 존재 여부 확인
        if (!lectureRepository.existsById(lectureId)) {
            throw new LectureException.LectureNotFoundException();
        }

        // 이미 평가를 작성했는지 확인
        if (lectureEvaluationRepository.existsByLectureIdAndUserId(lectureId, userId)) {
            throw new LectureException.DuplicateEvaluationException();
        }

        LectureEvaluation evaluation = new LectureEvaluation();
        evaluation.setLectureId(lectureId);
        evaluation.setUserId(userId);
        evaluation.setContent(request.getContent());
        evaluation.setDeliveryScore(request.getDeliveryScore());
        evaluation.setExpertiseScore(request.getExpertiseScore());
        evaluation.setGenerosityScore(request.getGenerosityScore());
        evaluation.setEffectivenessScore(request.getEffectivenessScore());
        evaluation.setCharacterScore(request.getCharacterScore());
        evaluation.setDifficultyScore(request.getDifficultyScore());
        evaluation.setAssignmentAmount(request.getAssignmentAmount());
        evaluation.setAssignmentDifficulty(request.getAssignmentDifficulty());
        evaluation.setSemester(request.getSemester());
        evaluation.setCreatedAt(LocalDateTime.now());
        evaluation.setUpdatedAt(LocalDateTime.now());

        LectureEvaluation savedEvaluation = lectureEvaluationRepository.save(evaluation);

        return new EvaluationResponse(
            savedEvaluation.getId(),
            savedEvaluation.getContent(),
            new EvaluationResponse.Scores(
                savedEvaluation.getDeliveryScore(),
                savedEvaluation.getExpertiseScore(),
                savedEvaluation.getGenerosityScore(),
                savedEvaluation.getEffectivenessScore(),
                savedEvaluation.getCharacterScore(),
                savedEvaluation.getDifficultyScore()
            ),
            new EvaluationResponse.AssignmentInfo(
                savedEvaluation.getAssignmentAmount().name(),
                savedEvaluation.getAssignmentDifficulty().name()
            ),
            savedEvaluation.getCreatedAt(),
            new EvaluationResponse.AuthorInfo(
                savedEvaluation.getUserId(),
                "User Nickname" // TODO: 사용자 닉네임 조회 로직 추가 필요
            )
        );
    }

    @Transactional
    public UpdatedEvaluationResponse updateEvaluation(Long lectureId, Long evaluationId, Long userId, UpdateEvaluationRequest request) {
        // 강의 존재 여부 확인
        if (!lectureRepository.existsById(lectureId)) {
            throw new LectureException.LectureNotFoundException();
        }

        // 평가 존재 여부 및 작성자 확인
        LectureEvaluation evaluation = lectureEvaluationRepository.findById(evaluationId)
                .orElseThrow(LectureException.EvaluationNotFoundException::new);

        if (!evaluation.getUserId().equals(userId)) {
            throw new LectureException.UnauthorizedEvaluationException();
        }

        if (!evaluation.getLectureId().equals(lectureId)) {
            throw new LectureException.InvalidEvaluationException();
        }

        // 선택적 필드 업데이트
        if (request.getContent() != null) {
            evaluation.setContent(request.getContent());
        }
        if (request.getDeliveryScore() != null) {
            evaluation.setDeliveryScore(request.getDeliveryScore());
        }
        if (request.getExpertiseScore() != null) {
            evaluation.setExpertiseScore(request.getExpertiseScore());
        }
        if (request.getGenerosityScore() != null) {
            evaluation.setGenerosityScore(request.getGenerosityScore());
        }
        if (request.getEffectivenessScore() != null) {
            evaluation.setEffectivenessScore(request.getEffectivenessScore());
        }
        if (request.getCharacterScore() != null) {
            evaluation.setCharacterScore(request.getCharacterScore());
        }
        if (request.getDifficultyScore() != null) {
            evaluation.setDifficultyScore(request.getDifficultyScore());
        }
        if (request.getAssignmentAmount() != null) {
            evaluation.setAssignmentAmount(request.getAssignmentAmount());
        }
        if (request.getAssignmentDifficulty() != null) {
            evaluation.setAssignmentDifficulty(request.getAssignmentDifficulty());
        }

        evaluation.setUpdatedAt(LocalDateTime.now());
        LectureEvaluation updatedEvaluation = lectureEvaluationRepository.save(evaluation);

        return new UpdatedEvaluationResponse(
            updatedEvaluation.getId(),
            updatedEvaluation.getContent(),
            new UpdatedEvaluationResponse.Scores(
                updatedEvaluation.getDeliveryScore(),
                updatedEvaluation.getExpertiseScore(),
                updatedEvaluation.getGenerosityScore(),
                updatedEvaluation.getEffectivenessScore(),
                updatedEvaluation.getCharacterScore(),
                updatedEvaluation.getDifficultyScore()
            ),
            new UpdatedEvaluationResponse.AssignmentInfo(
                updatedEvaluation.getAssignmentAmount().name(),
                updatedEvaluation.getAssignmentDifficulty().name()
            ),
            updatedEvaluation.getUpdatedAt()
        );
    }

    public List<LectureDetailResponse.EvaluationInfo> getEvaluationsByLectureId(Long lectureId) {
        return lectureEvaluationRepository.findByLectureId(lectureId).stream()
            .map(evaluation -> new LectureDetailResponse.EvaluationInfo(
                evaluation.getId(),
                evaluation.getContent(),
                new LectureDetailResponse.Scores(
                    evaluation.getDeliveryScore(),
                    evaluation.getExpertiseScore(),
                    evaluation.getGenerosityScore(),
                    evaluation.getEffectivenessScore(),
                    evaluation.getCharacterScore(),
                    evaluation.getDifficultyScore()
                ),
                new LectureDetailResponse.AssignmentInfo(
                    evaluation.getAssignmentAmount().name(),
                    evaluation.getAssignmentDifficulty().name()
                ),
                evaluation.getCreatedAt(),
                new LectureDetailResponse.AuthorInfo(
                    evaluation.getUserId(),
                    "User Nickname" // TODO: 사용자 닉네임 조회 로직 추가 필요
                )
            ))
            .collect(Collectors.toList());
    }

    public LectureDetailResponse.AverageScores getAverageScores(Long lectureId) {
        return new LectureDetailResponse.AverageScores(
            lectureEvaluationRepository.getAverageDeliveryScore(lectureId),
            lectureEvaluationRepository.getAverageExpertiseScore(lectureId),
            lectureEvaluationRepository.getAverageGenerosityScore(lectureId),
            lectureEvaluationRepository.getAverageEffectivenessScore(lectureId),
            lectureEvaluationRepository.getAverageCharacterScore(lectureId),
            lectureEvaluationRepository.getAverageDifficultyScore(lectureId)
        );
    }
} 