package com.example.arate.materials.service;

import com.example.arate.lectures.entity.Lecture;
import com.example.arate.lectures.exception.LectureException;
import com.example.arate.lectures.repository.LectureRepository;
import com.example.arate.materials.dto.CreateSharedMaterialRequest;
import com.example.arate.materials.dto.SharedMaterialResponse;
import com.example.arate.materials.dto.UpdateSharedMaterialRequest;
import com.example.arate.materials.entity.SharedMaterial;
import com.example.arate.materials.exception.MaterialException;
import com.example.arate.materials.repository.SharedMaterialRepository;
import com.example.arate.users.entity.User;
import com.example.arate.users.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SharedMaterialService {
    
    private final SharedMaterialRepository sharedMaterialRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 공유 자료 생성
     */
    @Transactional
    public SharedMaterialResponse createSharedMaterial(Long uploaderId, CreateSharedMaterialRequest request) {
        // 강의 존재 여부 확인
        Lecture lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(LectureException.LectureNotFoundException::new);
        
        // 업로더 정보 확인
        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 태그 리스트를 JSON 문자열로 변환
        String tagsJson = "[]";
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            try {
                tagsJson = objectMapper.writeValueAsString(request.getTags());
            } catch (JsonProcessingException e) {
                throw new MaterialException.TagProcessingException(e);
            }
        }
        
        // 공유 자료 엔티티 생성
        SharedMaterial sharedMaterial = new SharedMaterial();
        sharedMaterial.setUploaderId(uploaderId);
        sharedMaterial.setLectureId(request.getLectureId());
        sharedMaterial.setTitle(request.getTitle());
        sharedMaterial.setContent(request.getContent());
        sharedMaterial.setFile(request.getFile());
        sharedMaterial.setTags(tagsJson);
        sharedMaterial.setCreatedAt(LocalDateTime.now());
        
        SharedMaterial savedMaterial = sharedMaterialRepository.save(sharedMaterial);
        
        // JSON 문자열을 다시 리스트로 변환
        List<String> tagsList = new ArrayList<>();
        if (savedMaterial.getTags() != null && !savedMaterial.getTags().equals("[]")) {
            try {
                tagsList = objectMapper.readValue(savedMaterial.getTags(), List.class);
            } catch (JsonProcessingException e) {
                throw new MaterialException.TagProcessingException(e);
            }
        }
        
        return new SharedMaterialResponse(
            savedMaterial.getId(),
            savedMaterial.getUploaderId(),
            uploader.getName(),
            savedMaterial.getLectureId(),
            lecture.getTitle(),
            savedMaterial.getTitle(),
            savedMaterial.getContent(),
            savedMaterial.getFile(),
            tagsList,
            savedMaterial.getCreatedAt()
        );
    }
    
    /**
     * 공유 자료 목록 조회 (강의별)
     */
    public Page<SharedMaterialResponse> getSharedMaterialsByLecture(Long lectureId, Pageable pageable) {
        // 강의 존재 여부 확인
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(LectureException.LectureNotFoundException::new);
        
        return sharedMaterialRepository.findByLectureId(lectureId, pageable)
                .map(material -> {
                    // 업로더 정보 조회
                    User uploader = userRepository.findById(material.getUploaderId())
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                    
                    // JSON 문자열을 리스트로 변환
                    List<String> tagsList = new ArrayList<>();
                    if (material.getTags() != null && !material.getTags().equals("[]")) {
                        try {
                            tagsList = objectMapper.readValue(material.getTags(), List.class);
                        } catch (JsonProcessingException e) {
                            throw new MaterialException.TagProcessingException(e);
                        }
                    }
                    
                    return new SharedMaterialResponse(
                        material.getId(),
                        material.getUploaderId(),
                        uploader.getName(),
                        material.getLectureId(),
                        lecture.getTitle(),
                        material.getTitle(),
                        material.getContent(),
                        material.getFile(),
                        tagsList,
                        material.getCreatedAt()
                    );
                });
    }
    
    /**
     * 공유 자료 상세 조회
     */
    public SharedMaterialResponse getSharedMaterial(Long materialId) {
        SharedMaterial material = sharedMaterialRepository.findById(materialId)
                .orElseThrow(() -> new MaterialException.MaterialNotFoundException());
        
        // 강의 정보 조회
        Lecture lecture = lectureRepository.findById(material.getLectureId())
                .orElseThrow(LectureException.LectureNotFoundException::new);
        
        // 업로더 정보 조회
        User uploader = userRepository.findById(material.getUploaderId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // JSON 문자열을 리스트로 변환
        List<String> tagsList = new ArrayList<>();
        if (material.getTags() != null && !material.getTags().equals("[]")) {
            try {
                tagsList = objectMapper.readValue(material.getTags(), List.class);
            } catch (JsonProcessingException e) {
                throw new MaterialException.TagProcessingException(e);
            }
        }
        
        return new SharedMaterialResponse(
            material.getId(),
            material.getUploaderId(),
            uploader.getName(),
            material.getLectureId(),
            lecture.getTitle(),
            material.getTitle(),
            material.getContent(),
            material.getFile(),
            tagsList,
            material.getCreatedAt()
        );
    }
    
    /**
     * 공유 자료 수정
     */
    @Transactional
    public SharedMaterialResponse updateSharedMaterial(Long materialId, Long userId, UpdateSharedMaterialRequest request) {
        SharedMaterial material = sharedMaterialRepository.findById(materialId)
                .orElseThrow(() -> new MaterialException.MaterialNotFoundException());
        
        // 권한 확인
        if (!material.getUploaderId().equals(userId)) {
            throw new MaterialException.UnauthorizedMaterialException();
        }
        
        // 수정할 필드 업데이트
        if (request.getTitle() != null) {
            material.setTitle(request.getTitle());
        }
        
        if (request.getContent() != null) {
            material.setContent(request.getContent());
        }
        
        if (request.getFile() != null) {
            material.setFile(request.getFile());
        }
        
        // 태그 리스트를 JSON 문자열로 변환
        if (request.getTags() != null) {
            try {
                String tagsJson = objectMapper.writeValueAsString(request.getTags());
                material.setTags(tagsJson);
            } catch (JsonProcessingException e) {
                throw new MaterialException.TagProcessingException(e);
            }
        }
        
        SharedMaterial updatedMaterial = sharedMaterialRepository.save(material);
        
        // 강의 정보 조회
        Lecture lecture = lectureRepository.findById(updatedMaterial.getLectureId())
                .orElseThrow(LectureException.LectureNotFoundException::new);
        
        // 업로더 정보 조회
        User uploader = userRepository.findById(updatedMaterial.getUploaderId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // JSON 문자열을 리스트로 변환
        List<String> tagsList = new ArrayList<>();
        if (updatedMaterial.getTags() != null && !updatedMaterial.getTags().equals("[]")) {
            try {
                tagsList = objectMapper.readValue(updatedMaterial.getTags(), List.class);
            } catch (JsonProcessingException e) {
                throw new MaterialException.TagProcessingException(e);
            }
        }
        
        return new SharedMaterialResponse(
            updatedMaterial.getId(),
            updatedMaterial.getUploaderId(),
            uploader.getName(),
            updatedMaterial.getLectureId(),
            lecture.getTitle(),
            updatedMaterial.getTitle(),
            updatedMaterial.getContent(),
            updatedMaterial.getFile(),
            tagsList,
            updatedMaterial.getCreatedAt()
        );
    }
    
    /**
     * 공유 자료 삭제
     */
    @Transactional
    public void deleteSharedMaterial(Long materialId, Long userId) {
        SharedMaterial material = sharedMaterialRepository.findById(materialId)
                .orElseThrow(() -> new MaterialException.MaterialNotFoundException());
        
        // 권한 확인
        if (!material.getUploaderId().equals(userId)) {
            throw new MaterialException.UnauthorizedMaterialException();
        }
        
        sharedMaterialRepository.delete(material);
    }
    
    /**
     * 태그로 공유 자료 검색
     */
    public Page<SharedMaterialResponse> searchByTag(String tag, Pageable pageable) {
        return sharedMaterialRepository.findByTag(tag, pageable)
                .map(material -> {
                    // 강의 정보 조회
                    Lecture lecture = lectureRepository.findById(material.getLectureId())
                            .orElseThrow(LectureException.LectureNotFoundException::new);
                    
                    // 업로더 정보 조회
                    User uploader = userRepository.findById(material.getUploaderId())
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                    
                    // JSON 문자열을 리스트로 변환
                    List<String> tagsList = new ArrayList<>();
                    if (material.getTags() != null && !material.getTags().equals("[]")) {
                        try {
                            tagsList = objectMapper.readValue(material.getTags(), List.class);
                        } catch (JsonProcessingException e) {
                            throw new MaterialException.TagProcessingException(e);
                        }
                    }
                    
                    return new SharedMaterialResponse(
                        material.getId(),
                        material.getUploaderId(),
                        uploader.getName(),
                        material.getLectureId(),
                        lecture.getTitle(),
                        material.getTitle(),
                        material.getContent(),
                        material.getFile(),
                        tagsList,
                        material.getCreatedAt()
                    );
                });
    }
    
    /**
     * 키워드로 공유 자료 검색
     */
    public Page<SharedMaterialResponse> searchByKeyword(String keyword, Pageable pageable) {
        return sharedMaterialRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable)
                .map(material -> {
                    // 강의 정보 조회
                    Lecture lecture = lectureRepository.findById(material.getLectureId())
                            .orElseThrow(LectureException.LectureNotFoundException::new);
                    
                    // 업로더 정보 조회
                    User uploader = userRepository.findById(material.getUploaderId())
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                    
                    // JSON 문자열을 리스트로 변환
                    List<String> tagsList = new ArrayList<>();
                    if (material.getTags() != null && !material.getTags().equals("[]")) {
                        try {
                            tagsList = objectMapper.readValue(material.getTags(), List.class);
                        } catch (JsonProcessingException e) {
                            throw new MaterialException.TagProcessingException(e);
                        }
                    }
                    
                    return new SharedMaterialResponse(
                        material.getId(),
                        material.getUploaderId(),
                        uploader.getName(),
                        material.getLectureId(),
                        lecture.getTitle(),
                        material.getTitle(),
                        material.getContent(),
                        material.getFile(),
                        tagsList,
                        material.getCreatedAt()
                    );
                });
    }

    /**
     * 가장 최근에 업로드된 공유자료들을 조회합니다.
     * 대시보드용으로 사용됩니다.
     */
    public List<SharedMaterialResponse> getRecentMaterials(int limit) {
        List<SharedMaterial> materials = sharedMaterialRepository.findTop20ByOrderByCreatedAtDesc();
        
        return materials.stream()
                .limit(limit)
                .map(material -> {
                    // 강의 정보 조회
                    Lecture lecture = lectureRepository.findById(material.getLectureId())
                            .orElse(null);
                    
                    // 업로더 정보 조회
                    User uploader = userRepository.findById(material.getUploaderId())
                            .orElse(null);
                    
                    if (lecture == null || uploader == null) {
                        return null;
                    }
                    
                    // JSON 문자열을 리스트로 변환
                    List<String> tagsList = new ArrayList<>();
                    if (material.getTags() != null && !material.getTags().equals("[]")) {
                        try {
                            tagsList = objectMapper.readValue(material.getTags(), List.class);
                        } catch (JsonProcessingException e) {
                            // 에러 발생 시 빈 리스트로 처리
                            tagsList = new ArrayList<>();
                        }
                    }
                    
                    return new SharedMaterialResponse(
                        material.getId(),
                        material.getUploaderId(),
                        uploader.getName(),
                        material.getLectureId(),
                        lecture.getTitle(),
                        material.getTitle(),
                        material.getContent(),
                        material.getFile(),
                        tagsList,
                        material.getCreatedAt()
                    );
                })
                .filter(response -> response != null)
                .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }
} 