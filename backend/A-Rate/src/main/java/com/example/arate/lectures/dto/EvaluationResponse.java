package com.example.arate.lectures.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private Long id;
    private String content;
    private Scores scores;
    private AssignmentInfo assignment;
    private String examType;
    private Boolean teamProject;
    private LocalDateTime createdAt;
    private AuthorInfo author;
    private LectureInfo lecture;

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
    public static class LectureInfo {
        private Long id;
        private String title;
        private String professor;
        private String department;
    }
} 