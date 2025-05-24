package com.example.arate.lectures.service;

import com.example.arate.enrollments.service.EnrollmentService;
import com.example.arate.lectures.dto.*;
import com.example.arate.lectures.entity.LectureEvaluation;
import com.example.arate.lectures.exception.LectureException;
import com.example.arate.lectures.repository.LectureEvaluationRepository;
import com.example.arate.lectures.repository.LectureRepository;
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
    private final EnrollmentService enrollmentService;

    @Transactional
    public EvaluationResponse createEvaluation(Long lectureId, Long userId, CreateEvaluationRequest request) {
        // 강의 존재 여부 확인
        if (!lectureRepository.existsById(lectureId)) {
            throw new LectureException.LectureNotFoundException();
        }

        // 수강 인증 확인 (관리자에게 승인받은 학생만 강의평 작성 가능)
        if (!enrollmentService.canWriteEvaluation(userId, lectureId)) {
            throw new LectureException.NotCertifiedEnrollmentException();
        }

        // 이미 평가를 작성했는지 확인 (한 강의당 한 개의 평가만 작성 가능)
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
        evaluation.setExam(request.getExam());
        evaluation.setTeamProject(request.getTeamProject());
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
            savedEvaluation.getExam() != null ? savedEvaluation.getExam().name() : null,
            savedEvaluation.getTeamProject(),
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
        if (request.getExam() != null) {
            evaluation.setExam(request.getExam());
        }
        if (request.getTeamProject() != null) {
            evaluation.setTeamProject(request.getTeamProject());
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
            updatedEvaluation.getExam() != null ? updatedEvaluation.getExam().name() : null,
            updatedEvaluation.getTeamProject(),
            updatedEvaluation.getUpdatedAt()
        );
    }

    public List<LectureDetailResponse.EvaluationInfo> getEvaluationsByLectureId(Long lectureId) {
        return getEvaluationsByLectureId(lectureId, true); // 기본값은 모든 강의평 조회 가능
    }

    public List<LectureDetailResponse.EvaluationInfo> getEvaluationsByLectureId(Long lectureId, boolean hasWrittenEvaluation) {
        List<LectureEvaluation> evaluations = lectureEvaluationRepository.findByLectureId(lectureId);
        
        return evaluations.stream()
            .map(evaluation -> {
                // 첫 번째 강의평이거나 사용자가 강의평을 작성한 경우 전체 내용 공개
                boolean isFirstEvaluation = evaluations.indexOf(evaluation) == 0;
                boolean showFullContent = hasWrittenEvaluation || isFirstEvaluation;
                boolean isRestricted = !showFullContent;
                
                String content = showFullContent ? evaluation.getContent() : 
                    "강의평을 작성하면 더 많은 후기를 볼 수 있습니다. (맛보기)";
                
                String authorNickname = showFullContent ? "User Nickname" : "익명";
                
                return new LectureDetailResponse.EvaluationInfo(
                    evaluation.getId(),
                    content,
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
                    evaluation.getExam() != null ? evaluation.getExam().name() : null,
                    evaluation.getTeamProject(),
                    showFullContent ? evaluation.getCreatedAt() : null,
                    new LectureDetailResponse.AuthorInfo(
                        showFullContent ? evaluation.getUserId() : null,
                        authorNickname
                    ),
                    isRestricted
                );
            })
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
    
    @Transactional
    public void deleteEvaluation(Long lectureId, Long evaluationId, Long userId) {
        // 평가 존재 여부 및 작성자 확인
        LectureEvaluation evaluation = lectureEvaluationRepository.findById(evaluationId)
                .orElseThrow(LectureException.EvaluationNotFoundException::new);

        if (!evaluation.getUserId().equals(userId)) {
            throw new LectureException.UnauthorizedEvaluationException();
        }

        if (!evaluation.getLectureId().equals(lectureId)) {
            throw new LectureException.InvalidEvaluationException();
        }
        
        lectureEvaluationRepository.delete(evaluation);
    }

    public List<EvaluationResponse> getEvaluationsByUserId(Long userId) {
        List<LectureEvaluation> evaluations = lectureEvaluationRepository.findByUserId(userId);
        
        return evaluations.stream()
            .map(evaluation -> new EvaluationResponse(
                evaluation.getId(),
                evaluation.getContent(),
                new EvaluationResponse.Scores(
                    evaluation.getDeliveryScore(),
                    evaluation.getExpertiseScore(),
                    evaluation.getGenerosityScore(),
                    evaluation.getEffectivenessScore(),
                    evaluation.getCharacterScore(),
                    evaluation.getDifficultyScore()
                ),
                new EvaluationResponse.AssignmentInfo(
                    evaluation.getAssignmentAmount().name(),
                    evaluation.getAssignmentDifficulty().name()
                ),
                evaluation.getExam() != null ? evaluation.getExam().name() : null,
                evaluation.getTeamProject(),
                evaluation.getCreatedAt(),
                new EvaluationResponse.AuthorInfo(
                    evaluation.getUserId(),
                    "User Nickname" // TODO: 사용자 닉네임 조회 로직 추가 필요
                )
            ))
            .collect(Collectors.toList());
    }

    /**
     * 가장 최근에 작성된 강의평들을 조회합니다.
     * 대시보드용으로 사용됩니다.
     */
    public List<EvaluationResponse> getRecentEvaluations(int limit) {
        List<LectureEvaluation> evaluations = lectureEvaluationRepository.findTop20ByOrderByCreatedAtDesc();
        
        return evaluations.stream()
            .limit(limit)
            .map(evaluation -> new EvaluationResponse(
                evaluation.getId(),
                evaluation.getContent(),
                new EvaluationResponse.Scores(
                    evaluation.getDeliveryScore(),
                    evaluation.getExpertiseScore(),
                    evaluation.getGenerosityScore(),
                    evaluation.getEffectivenessScore(),
                    evaluation.getCharacterScore(),
                    evaluation.getDifficultyScore()
                ),
                new EvaluationResponse.AssignmentInfo(
                    evaluation.getAssignmentAmount().name(),
                    evaluation.getAssignmentDifficulty().name()
                ),
                evaluation.getExam() != null ? evaluation.getExam().name() : null,
                evaluation.getTeamProject(),
                evaluation.getCreatedAt(),
                new EvaluationResponse.AuthorInfo(
                    evaluation.getUserId(),
                    "User Nickname" // TODO: 사용자 닉네임 조회 로직 추가 필요
                )
            ))
            .collect(Collectors.toList());
    }
} 