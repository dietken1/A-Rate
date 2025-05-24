package com.example.arate.lectures.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureResponse {
    private Long id;
    private String title;
    private String professorName;
    private String department;
    private int evaluationCount;
    private double averageScore;
} 