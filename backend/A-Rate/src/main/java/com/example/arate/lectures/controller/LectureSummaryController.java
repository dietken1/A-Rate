package com.example.arate.lectures.controller;

import com.example.arate.lectures.dto.LectureSummaryResponse;
import com.example.arate.lectures.service.LectureSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/lectures")
@RequiredArgsConstructor
public class LectureSummaryController {
    private final LectureSummaryService lectureSummaryService;

    @GetMapping("/{lectureId}/summ")
    public ResponseEntity<LectureSummaryResponse> getLectureSummary(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureSummaryService.getLectureSummary(lectureId));
    }
} 