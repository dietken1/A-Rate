package com.example.arate.materials.controller;

import com.example.arate.materials.dto.CreateSharedMaterialRequest;
import com.example.arate.materials.dto.SharedMaterialResponse;
import com.example.arate.materials.dto.UpdateSharedMaterialRequest;
import com.example.arate.materials.service.SharedMaterialService;
import com.example.arate.users.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
public class SharedMaterialController {
    
    private final SharedMaterialService sharedMaterialService;
    
    /**
     * 공유 자료 목록 조회 안내 (기본 엔드포인트)
     */
    @GetMapping
    public ResponseEntity<String> getMaterialsInfo() {
        return ResponseEntity.ok("공유 자료 목록을 조회하려면 /lecture/{lectureId} 또는 /search 엔드포인트를 사용하세요.");
    }
    
    /**
     * 공유 자료 생성
     */
    @PostMapping
    public ResponseEntity<SharedMaterialResponse> createSharedMaterial(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateSharedMaterialRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sharedMaterialService.createSharedMaterial(userPrincipal.getId(), request));
    }
    
    /**
     * 공유 자료 목록 조회 (강의별)
     */
    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<Page<SharedMaterialResponse>> getSharedMaterialsByLecture(
            @PathVariable Long lectureId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.valueOf(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        return ResponseEntity.ok(sharedMaterialService.getSharedMaterialsByLecture(lectureId, pageable));
    }
    
    /**
     * 공유 자료 상세 조회
     */
    @GetMapping("/{materialId}")
    public ResponseEntity<SharedMaterialResponse> getSharedMaterial(@PathVariable Long materialId) {
        return ResponseEntity.ok(sharedMaterialService.getSharedMaterial(materialId));
    }
    
    /**
     * 공유 자료 수정
     */
    @PutMapping("/{materialId}")
    public ResponseEntity<SharedMaterialResponse> updateSharedMaterial(
            @PathVariable Long materialId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateSharedMaterialRequest request) {
        return ResponseEntity.ok(sharedMaterialService.updateSharedMaterial(materialId, userPrincipal.getId(), request));
    }
    
    /**
     * 공유 자료 삭제
     */
    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> deleteSharedMaterial(
            @PathVariable Long materialId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        sharedMaterialService.deleteSharedMaterial(materialId, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 태그로 공유 자료 검색
     */
    @GetMapping("/search/tag")
    public ResponseEntity<Page<SharedMaterialResponse>> searchByTag(
            @RequestParam String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sharedMaterialService.searchByTag(tag, pageable));
    }
    
    /**
     * 키워드로 공유 자료 검색
     */
    @GetMapping("/search/keyword")
    public ResponseEntity<Page<SharedMaterialResponse>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sharedMaterialService.searchByKeyword(keyword, pageable));
    }
} 