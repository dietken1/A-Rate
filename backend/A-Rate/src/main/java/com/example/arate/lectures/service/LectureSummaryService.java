package com.example.arate.lectures.service;

import com.example.arate.lectures.dto.LectureSummaryResponse;
import com.example.arate.lectures.entity.Lecture;
import com.example.arate.lectures.repository.LectureRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LectureSummaryService {
    private final LectureRepository lectureRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Transactional(readOnly = true)
    public LectureSummaryResponse getLectureSummary(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        // 모든 평가 내용을 하나의 문자열로 결합
        String allContents = lecture.getEvaluations().stream()
                .map(evaluation -> evaluation.getContent())
                .filter(content -> content != null && !content.trim().isEmpty())
                .reduce("", (a, b) -> a + " " + b);

        if (allContents.trim().isEmpty()) {
            return new LectureSummaryResponse(List.of());
        }

        // Gemini API 호출
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey;
        
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(
                    Map.of("text", "You are a Korean lecture evaluation analyzer. Analyze the following lecture evaluations and extract the most frequently mentioned terms related to the lecture. Return the result as a JSON array of objects with 'term' and 'frequency' fields. Only include terms that appear more than once. The response should be in Korean."),
                    Map.of("text", "Evaluations: " + allContents)
                )
            ))
        );

        try {
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");

            // JSON 응답을 파싱하여 LectureSummaryResponse 객체로 변환
            List<LectureSummaryResponse.TermFrequency> terms = objectMapper.readValue(
                text,
                objectMapper.getTypeFactory().constructCollectionType(
                    List.class,
                    LectureSummaryResponse.TermFrequency.class
                )
            );

            return new LectureSummaryResponse(terms);
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze lecture evaluations", e);
        }
    }
} 