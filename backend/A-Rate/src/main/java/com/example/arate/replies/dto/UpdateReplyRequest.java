package com.example.arate.replies.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReplyRequest {
    
    @NotBlank(message = "내용은 필수입니다")
    private String content;
} 