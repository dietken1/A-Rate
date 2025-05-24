package com.example.arate.lectures.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureSummaryResponse {
    private List<TermFrequency> frequentTerms;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermFrequency {
        private String term;
        private int frequency;
    }
} 