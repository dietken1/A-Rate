package com.example.arate.materials.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateSharedMaterialRequest {
    
    private String title;
    private String content;
    private String file;
    private List<String> tags;
} 