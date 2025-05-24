package com.example.arate.materials.repository;

import com.example.arate.materials.entity.SharedMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedMaterialRepository extends JpaRepository<SharedMaterial, Long> {
    
    Page<SharedMaterial> findByLectureId(Long lectureId, Pageable pageable);
    
    List<SharedMaterial> findByUploaderId(Long uploaderId);
    
    // 최신 공유자료 조회 (대시보드용)
    List<SharedMaterial> findTop20ByOrderByCreatedAtDesc();
    
    // 제목 또는 내용으로 검색
    Page<SharedMaterial> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    
    @Query("SELECT s FROM SharedMaterial s WHERE s.tags LIKE %:tag%")
    Page<SharedMaterial> findByTag(@Param("tag") String tag, Pageable pageable);
    
    @Query("SELECT s FROM SharedMaterial s WHERE " +
           "s.title LIKE %:keyword% OR " +
           "s.content LIKE %:keyword% OR " +
           "s.tags LIKE %:keyword%")
    Page<SharedMaterial> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
} 