package com.example.arate.enrollments.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    
    private Long id;
    private Long studentId;
    private Long lectureId;
    private String lectureTitle;
    private String certificationImage;
    private String grade;
    private Boolean isCertified;
    private String semester;
    private LocalDateTime createdAt;
    private LocalDateTime certifiedAt;
    
    // 학생 정보를 담는 내부 클래스
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentInfo {
        private Long id;
        private String name;
        private String studentNumber;
    }
    
    // 강의 정보를 담는 내부 클래스
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LectureInfo {
        private Long id;
        private String title;
        private String department;
        private String professorName;
    }
} 