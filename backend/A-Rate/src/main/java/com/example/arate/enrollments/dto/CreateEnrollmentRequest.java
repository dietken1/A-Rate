package com.example.arate.enrollments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateEnrollmentRequest {
    
    @NotNull(message = "강의 ID는 필수입니다")
    private Long lectureId;
    
    @NotBlank(message = "인증 이미지는 필수입니다")
    private String certificationImage;
    
    private String grade;
    
    @NotBlank(message = "학기는 필수입니다")
    private String semester;
} 