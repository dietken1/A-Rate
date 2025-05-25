package com.example.arate.replies.repository;

import com.example.arate.replies.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    
    List<Reply> findByEvaluation_Id(Long evaluationId);
    
    Page<Reply> findByEvaluation_Id(Long evaluationId, Pageable pageable);
    
    List<Reply> findByAuthor_Id(Long authorId);
    
    boolean existsByEvaluation_IdAndAuthor_Id(Long evaluationId, Long authorId);
} 