package com.example.arate.lectures.entity;

import com.example.arate.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_evaluations")
@Getter
@Setter
@NoArgsConstructor
public class LectureEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", referencedColumnName = "id", nullable = false)
    private Lecture lecture;

    private String semester;
    private String content;

    @Column(name = "delivery_score")
    private Integer deliveryScore;

    @Column(name = "expertise_score")
    private Integer expertiseScore;

    @Column(name = "generosity_score")
    private Integer generosityScore;

    @Column(name = "effectiveness_score")
    private Integer effectivenessScore;

    @Column(name = "character_score")
    private Integer characterScore;

    @Column(name = "difficulty_score")
    private Integer difficultyScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_amount")
    private AssignmentAmount assignmentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_difficulty")
    private AssignmentDifficulty assignmentDifficulty;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "exam")
    private ExamType exam;
    
    @Column(name = "team_project")
    private Boolean teamProject;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum AssignmentAmount {
        NONE, FEW, NORMAL, MANY
    }

    public enum AssignmentDifficulty {
        EASY, NORMAL, HARD
    }
    
    public enum ExamType {
        MIDTERM, FINAL, BOTH
    }
} 