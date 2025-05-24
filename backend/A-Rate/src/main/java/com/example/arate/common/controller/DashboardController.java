package com.example.arate.common.controller;

import com.example.arate.lectures.dto.LectureResponse;
import com.example.arate.lectures.dto.EvaluationResponse;
import com.example.arate.materials.dto.SharedMaterialResponse;
import com.example.arate.lectures.service.LectureService;
import com.example.arate.lectures.service.LectureEvaluationService;
import com.example.arate.materials.service.SharedMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "🏠 메인 대시보드", description = "메인페이지 요약 정보 API (비로그인 접근 가능)")
public class DashboardController {

    private final LectureService lectureService;
    private final LectureEvaluationService lectureEvaluationService;
    private final SharedMaterialService sharedMaterialService;

    @Operation(
        summary = "메인페이지 요약 정보 조회",
        description = """
            🏠 **메인페이지에 표시할 요약 정보를 한 번에 조회합니다.**
            
            ### 📊 포함 정보
            - **최고 평점 강의 3개**: 평점이 가장 높은 강의들
            - **최저 평점 강의 3개**: 평점이 가장 낮은 강의들 (주의 필요)
            - **최신 강의평 4개**: 가장 최근에 작성된 강의평들
            - **최신 공유자료 2개**: 가장 최근에 업로드된 자료들
            
            ### 🌟 특징
            - 비로그인 사용자도 접근 가능
            - 한 번의 API 호출로 모든 요약 정보 제공
            - 메인페이지 로딩 최적화
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 조회 성공",
            content = @Content(schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(name = "대시보드 요약 정보", value = """
                {
                  "topRatedLectures": [
                    {
                      "id": 1,
                      "title": "자료구조",
                      "professor": {
                        "name": "김교수",
                        "department": "컴퓨터공학과"
                      },
                      "averageRating": 4.8,
                      "evaluationCount": 24
                    }
                  ],
                  "bottomRatedLectures": [
                    {
                      "id": 5,
                      "title": "어려운 수학",
                      "professor": {
                        "name": "박교수",
                        "department": "수학과"
                      },
                      "averageRating": 2.1,
                      "evaluationCount": 8
                    }
                  ],
                  "recentEvaluations": [
                    {
                      "id": 10,
                      "content": "정말 좋은 강의였습니다!",
                      "createdAt": "2024-01-15T14:30:00",
                      "lecture": {
                        "title": "자료구조",
                        "professor": "김교수"
                      }
                    }
                  ],
                  "recentMaterials": [
                    {
                      "id": 3,
                      "title": "중간고사 정리노트",
                      "type": "PDF",
                      "createdAt": "2024-01-15T12:00:00",
                      "lecture": {
                        "title": "운영체제",
                        "professor": "이교수"
                      }
                    }
                  ]
                }
                """))),
        @ApiResponse(responseCode = "500", description = "❌ 서버 오류")
    })
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // 최고 평점 강의 3개
            List<LectureResponse> topRatedLectures = lectureService.getTopRatedLectures(3);
            summary.put("topRatedLectures", topRatedLectures);
            
            // 최저 평점 강의 3개  
            List<LectureResponse> bottomRatedLectures = lectureService.getBottomRatedLectures(3);
            summary.put("bottomRatedLectures", bottomRatedLectures);
            
            // 최신 강의평 4개
            List<EvaluationResponse> recentEvaluations = lectureEvaluationService.getRecentEvaluations(4);
            summary.put("recentEvaluations", recentEvaluations);
            
            // 최신 공유자료 2개
            List<SharedMaterialResponse> recentMaterials = sharedMaterialService.getRecentMaterials(2);
            summary.put("recentMaterials", recentMaterials);
            
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            // 에러 발생 시 빈 데이터로 응답
            summary.put("topRatedLectures", List.of());
            summary.put("bottomRatedLectures", List.of());
            summary.put("recentEvaluations", List.of());
            summary.put("recentMaterials", List.of());
            summary.put("message", "일부 데이터를 불러올 수 없습니다.");
            
            return ResponseEntity.ok(summary);
        }
    }

    @Operation(
        summary = "최고 평점 강의 목록",
        description = """
            ⭐ **평점이 가장 높은 강의들을 조회합니다.**
            
            ### 📊 정렬 기준
            - 평균 평점 기준 내림차순
            - 최소 3개 이상의 강의평이 있는 강의만 포함
            - 동점인 경우 강의평 개수가 많은 순서
            """
    )
    @GetMapping("/top-lectures")
    public ResponseEntity<List<LectureResponse>> getTopRatedLectures(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(lectureService.getTopRatedLectures(limit));
    }

    @Operation(
        summary = "최저 평점 강의 목록",
        description = """
            ⚠️ **평점이 가장 낮은 강의들을 조회합니다.**
            
            ### 📊 정렬 기준
            - 평균 평점 기준 오름차순
            - 최소 3개 이상의 강의평이 있는 강의만 포함
            - 수강 시 주의가 필요한 강의들
            """
    )
    @GetMapping("/bottom-lectures")
    public ResponseEntity<List<LectureResponse>> getBottomRatedLectures(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(lectureService.getBottomRatedLectures(limit));
    }

    @Operation(
        summary = "최신 강의평 목록",
        description = """
            🆕 **가장 최근에 작성된 강의평들을 조회합니다.**
            
            ### 📝 내용
            - 작성 시간 기준 내림차순
            - 강의 정보와 함께 표시
            - 비로그인 사용자는 내용 일부만 표시
            """
    )
    @GetMapping("/recent-evaluations")
    public ResponseEntity<List<EvaluationResponse>> getRecentEvaluations(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(lectureEvaluationService.getRecentEvaluations(limit));
    }

    @Operation(
        summary = "최신 공유자료 목록",
        description = """
            📁 **가장 최근에 업로드된 공유자료들을 조회합니다.**
            
            ### 📂 내용
            - 업로드 시간 기준 내림차순
            - 강의 정보와 함께 표시
            - 자료 유형별 아이콘 표시 가능
            """
    )
    @GetMapping("/recent-materials")
    public ResponseEntity<List<SharedMaterialResponse>> getRecentMaterials(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(sharedMaterialService.getRecentMaterials(limit));
    }
} 