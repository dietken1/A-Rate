package com.example.arate.materials.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_materials")
@Getter
@Setter
@NoArgsConstructor
public class SharedMaterial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;
    
    @Column(name = "lecture_id", nullable = false)
    private Long lectureId;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String file;
    
    @Column(columnDefinition = "JSON")
    private String tags;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 