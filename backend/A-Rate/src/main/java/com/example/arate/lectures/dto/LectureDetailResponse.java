package com.example.arate.lectures.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureDetailResponse {
    private Long id;
    private String title;
    private ProfessorInfo professor;
    private String department;
    private List<EvaluationInfo> evaluations;
    private AverageScores averageScores;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfessorInfo {
        private Long id;
        private String name;
        private String department;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluationInfo {
        private Long id;
        private String content;
        private Scores scores;
        private AssignmentInfo assignment;
        private LocalDateTime createdAt;
        private AuthorInfo author;
    }

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
        private String amount; // NONE, FEW, NORMAL, MANY
        private String difficulty; // EASY, NORMAL, HARD
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private Long id;
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AverageScores {
        private double delivery;
        private double expertise;
        private double generosity;
        private double effectiveness;
        private double character;
        private double difficulty;
    }
} 