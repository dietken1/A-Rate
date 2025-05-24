package com.example.arate.replies.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateReplyRequest {
    
    @NotNull(message = "평가 ID는 필수입니다")
    private Long evaluationId;
    
    @NotBlank(message = "내용은 필수입니다")
    private String content;
} 