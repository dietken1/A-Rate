package com.example.arate.replies.controller;

import com.example.arate.replies.dto.CreateReplyRequest;
import com.example.arate.replies.dto.ReplyResponse;
import com.example.arate.replies.dto.UpdateReplyRequest;
import com.example.arate.replies.service.ReplyService;
import com.example.arate.users.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/replies")
@RequiredArgsConstructor
public class ReplyController {
    
    private final ReplyService replyService;
    
    /**
     * 댓글 생성
     */
    @PostMapping
    public ResponseEntity<ReplyResponse> createReply(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateReplyRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(replyService.createReply(userPrincipal.getId(), request));
    }
    
    /**
     * 강의별 댓글 목록 조회 (해당 강의의 모든 평가에 달린 댓글들)
     */
    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<List<ReplyResponse>> getRepliesByLecture(
            @PathVariable Long lectureId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(replyService.getRepliesByLecture(lectureId, userPrincipal.getId()));
    }
    
    /**
     * 평가별 댓글 목록 조회
     */
    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<List<ReplyResponse>> getRepliesByEvaluation(
            @PathVariable Long evaluationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(replyService.getRepliesByEvaluation(evaluationId, userPrincipal.getId()));
    }
    
    /**
     * 댓글 상세 조회
     */
    @GetMapping("/{replyId}")
    public ResponseEntity<ReplyResponse> getReply(
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(replyService.getReply(replyId, userPrincipal.getId()));
    }
    
    /**
     * 댓글 수정
     */
    @PutMapping("/{replyId}")
    public ResponseEntity<ReplyResponse> updateReply(
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateReplyRequest request) {
        return ResponseEntity.ok(replyService.updateReply(replyId, userPrincipal.getId(), request));
    }
    
    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        replyService.deleteReply(replyId, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
} 