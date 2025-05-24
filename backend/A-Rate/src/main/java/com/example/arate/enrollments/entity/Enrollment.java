package com.example.arate.enrollments.entity;

import com.example.arate.users.entity.User;
import com.example.arate.lectures.entity.Lecture;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", referencedColumnName = "id", nullable = false)
    private Lecture lecture;
    
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