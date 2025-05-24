package com.example.arate.lectures.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedEvaluationResponse {
    private Long id;
    private String content;
    private Scores scores;
    private AssignmentInfo assignment;
    private LocalDateTime updatedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Scores {
        private int delivery;
        private int expertise;
        private int generosity;
        private int effectiveness;
        private int character;
        private int difficulty;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentInfo {
        private String amount;
        private String difficulty;
    }
} 