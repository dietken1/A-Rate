package com.example.arate.lecture.dto;

import com.example.arate.lecture.entity.LectureEvaluation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateEvaluationRequest {
    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @NotNull(message = "강의 전달력 점수는 필수입니다")
    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer deliveryScore;

    @NotNull(message = "전문성 점수는 필수입니다")
    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer expertiseScore;

    @NotNull(message = "관대함 점수는 필수입니다")
    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer generosityScore;

    @NotNull(message = "효율성 점수는 필수입니다")
    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer effectivenessScore;

    @NotNull(message = "인성 점수는 필수입니다")
    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer characterScore;

    @NotNull(message = "난이도 점수는 필수입니다")
    @Min(value = 1, message = "점수는 1점 이상이어야 합니다")
    @Max(value = 5, message = "점수는 5점 이하여야 합니다")
    private Integer difficultyScore;

    @NotNull(message = "과제량은 필수입니다")
    private LectureEvaluation.AssignmentAmount assignmentAmount;

    @NotNull(message = "과제 난이도는 필수입니다")
    private LectureEvaluation.AssignmentDifficulty assignmentDifficulty;

    @NotBlank(message = "학기는 필수입니다")
    private String semester;
} 