package com.example.arate.enrollments.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "lecture_id", nullable = false)
    private Long lectureId;
    
    @Column(name = "certification_image")
    private String certificationImage;
    
    private String grade;
    
    @Column(name = "is_certified")
    private Boolean isCertified = false;
    
    private String semester;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "certified_at")
    private LocalDateTime certifiedAt;
} 