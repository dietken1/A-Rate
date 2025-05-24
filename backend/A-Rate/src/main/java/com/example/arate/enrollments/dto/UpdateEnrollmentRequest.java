package com.example.arate.enrollments.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateEnrollmentRequest {
    
    private String certificationImage;
    private String grade;
    private String semester;
    private Boolean isCertified;
} 