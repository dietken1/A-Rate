package com.example.arate.lectures.controller;

import com.example.arate.lectures.dto.*;
import com.example.arate.lectures.exception.LectureException;
import com.example.arate.lectures.service.LectureEvaluationService;
import com.example.arate.lectures.service.LectureService;
import com.example.arate.users.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lectures")
@RequiredArgsConstructor
@Tag(name = "🎓 강의 관리", description = "강의 조회 및 강의평 관리 API")
public class LectureController {
    private final LectureService lectureService;
    private final LectureEvaluationService lectureEvaluationService;

    @Operation(
        summary = "강의 목록 조회",
        description = """
            📋 **강의 목록을 페이징으로 조회합니다.**
            
            ### 🔍 검색 및 필터링 옵션
            - **keyword**: 교수명과 과목명을 동시에 검색 (부분 검색 지원)
              - 예: "김"으로 검색하면 "김교수" 강의와 "김치학" 과목 모두 검색
              - 예: "확률"로 검색하면 "확률과 통계" 과목과 "확률" 교수 모두 검색
            - **department**: 학과명으로 필터링 ("all"이면 전체)
            - **courseType**: 학수구분으로 필터링 ("all"이면 전체)
            - **professorId**: 교수 ID로 필터링 (기존 호환성)
            - **page**: 페이지 번호 (0부터 시작)
            - **size**: 페이지 크기 (기본값: 20)
            
            ### 💡 사용 예시
            - 전체 조회: `/lectures`
            - 키워드 검색: `/lectures?keyword=확률`
            - 학과 필터링: `/lectures?keyword=김&department=컴퓨터공학과`
            - 학수구분 필터링: `/lectures?courseType=전공필수`
            - 복합 검색: `/lectures?keyword=확률&department=수학과&courseType=전공선택`
            
            ### 🌟 특징
            - 비로그인 사용자도 조회 가능
            - 강의별 평점 평균과 강의평 개수 포함
            - 키워드는 교수명과 과목명을 OR 조건으로 검색
            """,
        parameters = {
            @Parameter(name = "keyword", description = "검색 키워드 (교수명 + 과목명 동시 검색)", example = "확률"),
            @Parameter(name = "department", description = "학과명 (all=전체)", example = "컴퓨터공학과"),
            @Parameter(name = "courseType", description = "학수구분 (all=전체)", example = "전공필수"),
            @Parameter(name = "professorId", description = "교수 ID (기존 호환성)", example = "1"),
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
            @Parameter(name = "size", description = "페이지 크기", example = "20")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 조회 성공",
            content = @Content(schema = @Schema(implementation = Page.class),
                examples = @ExampleObject(name = "검색 결과 예시", value = """
                {
                  "content": [
                    {
                      "id": 1,
                      "title": "확률과 통계",
                      "professorName": "김교수",
                      "department": "수학과",
                      "evaluationCount": 15,
                      "averageScore": 4.2
                    }
                  ],
                  "totalElements": 1,
                  "totalPages": 1,
                  "first": true,
                  "last": true
                }
                """))),
        @ApiResponse(responseCode = "400", description = "❌ 잘못된 요청 파라미터")
    })
    @GetMapping
    public ResponseEntity<Page<LectureResponse>> getLectures(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "all") String department,
            @RequestParam(defaultValue = "all") String courseType,
            @RequestParam(required = false) Long professorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(lectureService.getLectures(keyword, department, courseType, professorId, page, size));
    }

    @Operation(
        summary = "강의 상세 조회",
        description = """
            📖 **특정 강의의 상세 정보와 강의평을 조회합니다.**
            
            ### 🎯 맛보기 시스템
            - **비로그인 사용자**: 첫 번째 강의평만 완전히 볼 수 있음
            - **강의평 미작성자**: 첫 번째 강의평만 완전히 볼 수 있음
            - **강의평 작성자**: 모든 강의평을 완전히 볼 수 있음
            - **제한된 강의평**: `isRestricted: true`로 표시됨
            
            ### 📊 포함 정보
            - 강의 기본 정보 (제목, 교수, 학과)
            - 강의평 목록 (조건부 공개)
            - 평점 평균 (전달력, 전문성, 너그러움 등)
            """,
        parameters = {
            @Parameter(name = "lectureId", description = "강의 ID", example = "1", required = true)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 조회 성공",
            content = @Content(schema = @Schema(implementation = LectureDetailResponse.class),
                examples = @ExampleObject(name = "강의 상세 조회 예시", value = """
                {
                  "id": 1,
                  "title": "자료구조",
                  "professor": {
                    "id": 2,
                    "name": "김교수",
                    "department": "컴퓨터공학과"
                  },
                  "department": "컴퓨터공학과",
                  "evaluations": [
                    {
                      "id": 1,
                      "content": "정말 좋은 강의였습니다!",
                      "scores": {
                        "delivery": 5,
                        "expertise": 5,
                        "generosity": 4,
                        "effectiveness": 5,
                        "character": 5,
                        "difficulty": 3
                      },
                      "assignment": {
                        "amount": "NORMAL",
                        "difficulty": "NORMAL"
                      },
                      "examType": "MIDTERM_FINAL",
                      "teamProject": true,
                      "createdAt": "2024-01-15T10:30:00",
                      "author": {
                        "id": 3,
                        "nickname": "학생A"
                      },
                      "isRestricted": false
                    }
                  ],
                  "averageScores": {
                    "delivery": 4.5,
                    "expertise": 4.8,
                    "generosity": 4.2,
                    "effectiveness": 4.6,
                    "character": 4.7,
                    "difficulty": 3.2
                  }
                }
                """))),
        @ApiResponse(responseCode = "404", description = "❌ 강의를 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = """
            {
              "message": "강의를 찾을 수 없습니다."
            }
            """)))
    })
    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureDetailResponse> getLectureDetail(@PathVariable Long lectureId) {
        // 현재 인증된 사용자 정보를 가져옴 (인증되지 않은 경우 null)
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication.getPrincipal() instanceof String)) { // "anonymousUser"가 아닌 경우
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            // 로그인 사용자: 강의평 작성 여부에 따라 다른 응답
            return ResponseEntity.ok(lectureService.getLectureDetailWithUser(lectureId, userPrincipal.getId()));
        } else {
            // 비로그인 사용자: 첫 번째 강의평만 맛보기로 제공
            return ResponseEntity.ok(lectureService.getLectureDetail(lectureId));
        }
    }

    @Operation(
        summary = "강의평 작성",
        description = """
            ✍️ **새로운 강의평을 작성합니다.**
            
            ### ⚠️ 작성 조건
            1. **로그인 필수**: JWT 토큰 인증 필요
            2. **수강 인증 필수**: 관리자가 해당 강의 수강을 승인한 학생만 가능
            3. **중복 작성 불가**: 한 강의당 한 개의 강의평만 작성 가능
            
            ### 📝 작성 항목
            - **텍스트 후기**: 강의에 대한 자유로운 후기
            - **6개 평점**: 전달력, 전문성, 너그러움, 효율성, 인격, 난이도 (1-5점)
            - **과제 정보**: 과제량, 과제 난이도
            - **기타 정보**: 시험 형태, 팀 프로젝트 여부, 수강 학기
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "✅ 강의평 작성 성공",
            content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
        @ApiResponse(responseCode = "400", description = "❌ 잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요"),
        @ApiResponse(responseCode = "403", description = "❌ 수강 인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "❌ 강의를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "❌ 이미 강의평을 작성한 강의")
    })
    @PostMapping("/{lectureId}/evaluations")
    public ResponseEntity<EvaluationResponse> createEvaluation(
            @Parameter(description = "강의 ID", example = "1")
            @PathVariable Long lectureId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "강의평 작성 데이터",
                content = @Content(schema = @Schema(implementation = CreateEvaluationRequest.class),
                    examples = @ExampleObject(name = "강의평 작성 예시", value = """
                    {
                      "content": "정말 유익한 강의였습니다. 교수님이 친절하시고 설명도 명확해서 이해하기 쉬웠어요.",
                      "deliveryScore": 5,
                      "expertiseScore": 4,
                      "generosityScore": 5,
                      "effectivenessScore": 4,
                      "characterScore": 5,
                      "difficultyScore": 3,
                      "assignmentAmount": "NORMAL",
                      "assignmentDifficulty": "NORMAL",
                      "exam": "MIDTERM_FINAL",
                      "teamProject": true,
                      "semester": "2024-1"
                    }
                    """)))
            @Valid @RequestBody CreateEvaluationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lectureEvaluationService.createEvaluation(lectureId, userPrincipal.getId(), request));
    }

    @Operation(
        summary = "강의평 수정",
        description = """
            ✏️ **본인이 작성한 강의평을 수정합니다.**
            
            ### 🔒 수정 권한
            - 본인이 작성한 강의평만 수정 가능
            - 모든 필드는 선택적 업데이트 (일부만 수정 가능)
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 수정 성공"),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요"),
        @ApiResponse(responseCode = "403", description = "❌ 수정 권한 없음 (본인 강의평 아님)"),
        @ApiResponse(responseCode = "404", description = "❌ 강의평을 찾을 수 없음")
    })
    @PatchMapping("/{lectureId}/evaluations/{evaluationId}")
    public ResponseEntity<UpdatedEvaluationResponse> updateEvaluation(
            @Parameter(description = "강의 ID", example = "1") @PathVariable Long lectureId,
            @Parameter(description = "강의평 ID", example = "1") @PathVariable Long evaluationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateEvaluationRequest request) {
        return ResponseEntity.ok(lectureEvaluationService.updateEvaluation(lectureId, evaluationId, userPrincipal.getId(), request));
    }

    @Operation(
        summary = "강의평 삭제",
        description = """
            🗑️ **본인이 작성한 강의평을 삭제합니다.**
            
            ### 🔒 삭제 권한
            - 본인이 작성한 강의평만 삭제 가능
            - 삭제 후 복구 불가능
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "✅ 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요"),
        @ApiResponse(responseCode = "403", description = "❌ 삭제 권한 없음 (본인 강의평 아님)"),
        @ApiResponse(responseCode = "404", description = "❌ 강의평을 찾을 수 없음")
    })
    @DeleteMapping("/{lectureId}/evaluations/{evaluationId}")
    public ResponseEntity<Void> deleteEvaluation(
            @Parameter(description = "강의 ID", example = "1") @PathVariable Long lectureId,
            @Parameter(description = "강의평 ID", example = "1") @PathVariable Long evaluationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        lectureEvaluationService.deleteEvaluation(lectureId, evaluationId, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "내 강의 목록 조회 (교수 전용)",
        description = """
            👨‍🏫 **교수가 본인이 담당하는 강의 목록을 조회합니다.**
            
            ### 🎯 교수 전용 기능
            - 교수 권한 필수
            - 본인이 담당하는 강의만 조회
            - 강의별 평점과 강의평 개수 확인 가능
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 조회 성공"),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요"),
        @ApiResponse(responseCode = "403", description = "❌ 교수 권한 필요")
    })
    @GetMapping("/my")
    public ResponseEntity<Page<LectureResponse>> getMyLectures(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        // 교수만 접근 가능
        if (!userPrincipal.isProfessor()) {
            throw new AccessDeniedException("교수만 접근할 수 있습니다.");
        }
        // 본인이 담당하는 강의만 조회 (professorId로 필터링)
        return ResponseEntity.ok(lectureService.getLectures(null, userPrincipal.getId(), 0, 100));
    }

    @Operation(
        summary = "내가 작성한 강의평 목록 조회",
        description = """
            📝 **본인이 작성한 모든 강의평을 조회합니다.**
            
            ### ✨ 기능
            - 작성한 강의평 전체 목록
            - 강의별 작성 날짜 확인 가능
            - 수정/삭제 시 참고용으로 활용
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 조회 성공"),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요")
    })
    @GetMapping("/evaluations/my")
    public ResponseEntity<List<EvaluationResponse>> getMyEvaluations(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(lectureEvaluationService.getEvaluationsByUserId(userPrincipal.getId()));
    }

    @Operation(
        summary = "강의평 작성",
        description = """
            ✍️ **새로운 강의평을 작성합니다.**
            
            ### ⚠️ 작성 조건
            1. **로그인 필수**: JWT 토큰 인증 필요
            2. **수강 인증 필수**: 관리자가 해당 강의 수강을 승인한 학생만 가능
            3. **중복 작성 불가**: 한 강의당 한 개의 강의평만 작성 가능
            
            ### 📝 작성 항목
            - **텍스트 후기**: 강의에 대한 자유로운 후기
            - **6개 평점**: 전달력, 전문성, 너그러움, 효율성, 인격, 난이도 (1-5점)
            - **과제 정보**: 과제량, 과제 난이도
            - **기타 정보**: 시험 형태, 팀 프로젝트 여부, 수강 학기
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "✅ 강의평 작성 성공",
            content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
        @ApiResponse(responseCode = "400", description = "❌ 잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요"),
        @ApiResponse(responseCode = "403", description = "❌ 수강 인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "❌ 강의를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "❌ 이미 강의평을 작성한 강의")
    })
    @GetMapping("/{lectureId}/evaluations")
    public ResponseEntity<LectureDetailResponse> getLectureEvaluations(
            @Parameter(description = "강의 ID", example = "1")
            @PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureService.getLectureDetail(lectureId));
    }
} 