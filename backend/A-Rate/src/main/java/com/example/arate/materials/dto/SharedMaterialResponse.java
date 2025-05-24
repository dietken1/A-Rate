package com.example.arate.materials.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SharedMaterialResponse {
    
    private Long id;
    private Long uploaderId;
    private String uploaderName;
    private Long lectureId;
    private String lectureTitle;
    private String title;
    private String content;
    private String file;
    private List<String> tags;
    private LocalDateTime createdAt;
} 