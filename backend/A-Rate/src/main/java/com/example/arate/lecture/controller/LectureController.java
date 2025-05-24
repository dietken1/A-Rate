package com.example.arate.lecture.controller;

import com.example.arate.lecture.dto.*;
import com.example.arate.lecture.service.LectureEvaluationService;
import com.example.arate.lecture.service.LectureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lectures")
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;
    private final LectureEvaluationService lectureEvaluationService;

    @GetMapping
    public ResponseEntity<Page<LectureResponse>> getLectures(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Long professorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(lectureService.getLectures(department, professorId, page, size));
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureDetailResponse> getLectureDetail(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureService.getLectureDetail(lectureId));
    }

    @PostMapping("/{lectureId}/evaluations")
    public ResponseEntity<EvaluationResponse> createEvaluation(
            @PathVariable Long lectureId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateEvaluationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lectureEvaluationService.createEvaluation(lectureId, userId, request));
    }

    @PatchMapping("/{lectureId}/evaluations/{evaluationId}")
    public ResponseEntity<UpdatedEvaluationResponse> updateEvaluation(
            @PathVariable Long lectureId,
            @PathVariable Long evaluationId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateEvaluationRequest request) {
        return ResponseEntity.ok(lectureEvaluationService.updateEvaluation(lectureId, evaluationId, userId, request));
    }
} 