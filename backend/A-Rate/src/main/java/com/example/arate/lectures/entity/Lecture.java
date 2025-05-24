package com.example.arate.lectures.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lectures")
@Getter
@Setter
@NoArgsConstructor
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @Column(nullable = false)
    private String title;

    @Column(name = "professor_ref_id")
    private Long professorRefId;

    private String department;
    
    @Column(name = "course_type")
    private String courseType; // 학수구분: "전공필수", "전공선택", "교양필수", "교양선택" 등
} 