package com.example.arate.materials.entity;

import com.example.arate.users.entity.User;
import com.example.arate.lectures.entity.Lecture;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", referencedColumnName = "id", nullable = false)
    private User uploader;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", referencedColumnName = "id", nullable = false)
    private Lecture lecture;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String file;
    
    @Column(columnDefinition = "JSON")
    private String tags;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 