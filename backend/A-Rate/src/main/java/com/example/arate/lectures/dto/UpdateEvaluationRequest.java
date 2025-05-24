package com.example.arate.lectures.dto;

import com.example.arate.lectures.entity.LectureEvaluation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateEvaluationRequest {
    private String content;

    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer deliveryScore;

    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer expertiseScore;

    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer generosityScore;

    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer effectivenessScore;

    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer characterScore;

    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer difficultyScore;

    private LectureEvaluation.AssignmentAmount assignmentAmount;
    private LectureEvaluation.AssignmentDifficulty assignmentDifficulty;
} 