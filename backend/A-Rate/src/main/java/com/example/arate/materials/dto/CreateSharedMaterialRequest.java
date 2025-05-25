package com.example.arate.materials.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateSharedMaterialRequest {
    
    @NotNull(message = "강의 ID는 필수입니다")
    private Long lectureId;
    
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    
    private String content;
    
    private String file;
    
    private List<String> tags;
} 