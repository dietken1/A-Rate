package com.example.arate.replies.service;

import com.example.arate.enrollments.exception.EnrollmentException;
import com.example.arate.enrollments.service.EnrollmentService;
import com.example.arate.lectures.entity.LectureEvaluation;
import com.example.arate.lectures.exception.LectureException;
import com.example.arate.lectures.repository.LectureEvaluationRepository;
import com.example.arate.replies.dto.CreateReplyRequest;
import com.example.arate.replies.dto.ReplyResponse;
import com.example.arate.replies.dto.UpdateReplyRequest;
import com.example.arate.replies.entity.Reply;
import com.example.arate.replies.exception.ReplyException;
import com.example.arate.replies.repository.ReplyRepository;
import com.example.arate.users.entity.User;
import com.example.arate.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {
    
    private final ReplyRepository replyRepository;
    private final LectureEvaluationRepository lectureEvaluationRepository;
    private final UserRepository userRepository;
    private final EnrollmentService enrollmentService;
    
    /**
     * 댓글 생성
     */
    @Transactional
    public ReplyResponse createReply(Long authorId, CreateReplyRequest request) {
        // 평가 존재 여부 확인
        LectureEvaluation evaluation = lectureEvaluationRepository.findById(request.getEvaluationId())
                .orElseThrow(() -> new LectureException.EvaluationNotFoundException());
        
        // 작성자 정보 확인
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 강의평 조회 자격 확인 (최소 1개 이상의 강의평을 작성했는지)
        if (!enrollmentService.canViewEvaluations(authorId)) {
            throw new EnrollmentException.NoEvaluationWrittenException();
        }
        
        // 댓글 엔티티 생성
        Reply reply = new Reply();
        reply.setEvaluation(evaluation);
        reply.setAuthor(author);
        reply.setContent(request.getContent());
        reply.setCreatedAt(LocalDateTime.now());
        
        Reply savedReply = replyRepository.save(reply);
        
        return new ReplyResponse(
            savedReply.getId(),
            savedReply.getEvaluation().getId(),
            savedReply.getAuthor().getId(),
            author.getName(),
            savedReply.getContent(),
            savedReply.getCreatedAt(),
            savedReply.getUpdatedAt()
        );
    }
    
    /**
     * 강의별 댓글 목록 조회 (해당 강의의 모든 평가에 달린 댓글들)
     */
    public List<ReplyResponse> getRepliesByLecture(Long lectureId, Long userId) {
        // 강의평 조회 자격 확인 (최소 1개 이상의 강의평을 작성했는지)
        if (!enrollmentService.canViewEvaluations(userId)) {
            throw new EnrollmentException.NoEvaluationWrittenException();
        }
        
        // 해당 강의의 모든 평가에 달린 댓글들을 수집
        return lectureEvaluationRepository.findByLecture_Id(lectureId)
                .stream()
                .flatMap(evaluation -> replyRepository.findByEvaluation_Id(evaluation.getId()).stream())
                .map(reply -> {
                    return new ReplyResponse(
                        reply.getId(),
                        reply.getEvaluation().getId(),
                        reply.getAuthor().getId(),
                        reply.getAuthor().getName(),
                        reply.getContent(),
                        reply.getCreatedAt(),
                        reply.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 평가별 댓글 목록 조회
     */
    public List<ReplyResponse> getRepliesByEvaluation(Long evaluationId, Long userId) {
        // 강의평 조회 자격 확인 (최소 1개 이상의 강의평을 작성했는지)
        if (!enrollmentService.canViewEvaluations(userId)) {
            throw new EnrollmentException.NoEvaluationWrittenException();
        }
        
        return replyRepository.findByEvaluation_Id(evaluationId).stream()
                .map(reply -> {
                    return new ReplyResponse(
                        reply.getId(),
                        reply.getEvaluation().getId(),
                        reply.getAuthor().getId(),
                        reply.getAuthor().getName(),
                        reply.getContent(),
                        reply.getCreatedAt(),
                        reply.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 댓글 상세 조회
     */
    public ReplyResponse getReply(Long replyId, Long userId) {
        // 강의평 조회 자격 확인 (최소 1개 이상의 강의평을 작성했는지)
        if (!enrollmentService.canViewEvaluations(userId)) {
            throw new EnrollmentException.NoEvaluationWrittenException();
        }
        
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ReplyException.ReplyNotFoundException());
        
        return new ReplyResponse(
            reply.getId(),
            reply.getEvaluation().getId(),
            reply.getAuthor().getId(),
            reply.getAuthor().getName(),
            reply.getContent(),
            reply.getCreatedAt(),
            reply.getUpdatedAt()
        );
    }
    
    /**
     * 댓글 수정
     */
    @Transactional
    public ReplyResponse updateReply(Long replyId, Long userId, UpdateReplyRequest request) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ReplyException.ReplyNotFoundException());
        
        // 권한 확인
        if (!reply.getAuthor().getId().equals(userId)) {
            throw new ReplyException.UnauthorizedReplyException();
        }
        
        // 내용 업데이트
        reply.setContent(request.getContent());
        reply.setUpdatedAt(LocalDateTime.now());
        
        Reply updatedReply = replyRepository.save(reply);
        
        return new ReplyResponse(
            updatedReply.getId(),
            updatedReply.getEvaluation().getId(),
            updatedReply.getAuthor().getId(),
            updatedReply.getAuthor().getName(),
            updatedReply.getContent(),
            updatedReply.getCreatedAt(),
            updatedReply.getUpdatedAt()
        );
    }
    
    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteReply(Long replyId, Long userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ReplyException.ReplyNotFoundException());
        
        // 권한 확인
        if (!reply.getAuthor().getId().equals(userId)) {
            throw new ReplyException.UnauthorizedReplyException();
        }
        
        replyRepository.delete(reply);
    }
    
    /**
     * 사용자가 작성한 댓글 목록 조회
     */
    public List<ReplyResponse> getRepliesByUser(Long userId) {
        return replyRepository.findByAuthor_Id(userId).stream()
                .map(reply -> new ReplyResponse(
                    reply.getId(),
                    reply.getEvaluation().getId(),
                    reply.getAuthor().getId(),
                    reply.getAuthor().getName(),
                    reply.getContent(),
                    reply.getCreatedAt(),
                    reply.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
} 