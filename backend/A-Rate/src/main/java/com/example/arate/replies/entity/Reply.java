package com.example.arate.replies.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "replies")
@Getter
@Setter
@NoArgsConstructor
public class Reply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "evaluation_id", nullable = false)
    private Long evaluationId;
    
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    
    private String content;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 