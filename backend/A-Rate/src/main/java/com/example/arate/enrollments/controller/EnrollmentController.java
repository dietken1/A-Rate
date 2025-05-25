package com.example.arate.enrollments.controller;

import com.example.arate.enrollments.dto.CreateEnrollmentRequest;
import com.example.arate.enrollments.dto.EnrollmentResponse;
import com.example.arate.enrollments.dto.UpdateEnrollmentRequest;
import com.example.arate.enrollments.service.EnrollmentService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Tag(name = "📋 수강 인증", description = "수강 인증 요청 및 관리자 승인 API")
public class EnrollmentController {
    
    private final EnrollmentService enrollmentService;
    
    @Operation(
        summary = "수강 인증 요청",
        description = """
            📤 **학생이 특정 강의의 수강 인증을 요청합니다.**
            
            ### 📸 인증 과정
            1. **수강 증빙 자료 업로드**: 성적표, 수강신청 확인서 등의 이미지
            2. **수강 정보 입력**: 학점, 수강 학기 정보
            3. **관리자 승인 대기**: 관리자가 자료를 검토 후 승인/거부
            4. **승인 완료**: 강의평 작성 권한 획득
            
            ### ⚠️ 주의사항
            - 강의당 한 번만 인증 요청 가능
            - 이미 인증 요청한 강의는 중복 요청 불가
            - 승인된 후에만 해당 강의에 대한 강의평 작성 가능
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "✅ 인증 요청 성공",
            content = @Content(schema = @Schema(implementation = EnrollmentResponse.class))),
        @ApiResponse(responseCode = "400", description = "❌ 잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요"),
        @ApiResponse(responseCode = "404", description = "❌ 강의를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "❌ 이미 인증 요청한 강의")
    })
    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "수강 인증 요청 데이터",
                content = @Content(schema = @Schema(implementation = CreateEnrollmentRequest.class),
                    examples = @ExampleObject(name = "수강 인증 요청 예시", value = """
                    {
                      "lectureId": 1,
                      "certificationImage": "https://example.com/images/transcript.jpg",
                      "grade": "A+",
                      "semester": "2024-1"
                    }
                    """)))
            @Valid @RequestBody CreateEnrollmentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(enrollmentService.createEnrollment(userPrincipal.getId(), request));
    }
    
    @Operation(
        summary = "내 수강 인증 요청 목록 조회",
        description = """
            📋 **본인이 요청한 수강 인증 목록을 조회합니다.**
            
            ### 📊 확인 가능한 정보
            - **요청 상태**: 대기중/승인완료/거부
            - **요청 일시**: 언제 인증을 요청했는지
            - **승인 일시**: 언제 승인되었는지 (승인된 경우)
            - **강의 정보**: 어떤 강의에 대한 인증인지
            - **제출 자료**: 업로드한 증빙 이미지
            
            ### 💡 활용 방법
            - 승인 상태 확인
            - 거부된 경우 재요청 검토
            - 강의평 작성 가능 여부 확인
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 조회 성공",
            content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요")
    })
    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getEnrollments(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudentId(userPrincipal.getId()));
    }
    
    @Operation(
        summary = "수강 인증 요청 삭제",
        description = """
            🗑️ **본인이 요청한 수강 인증을 삭제합니다.**
            
            ### 🔄 삭제 가능한 경우
            - **대기 중인 요청**: 아직 관리자가 검토하지 않은 요청
            - **거부된 요청**: 다시 요청하기 전에 삭제 가능
            
            ### ⚠️ 주의사항
            - 승인된 인증은 삭제 불가
            - 삭제 후 동일 강의에 대해 재요청 가능
            - 본인이 요청한 것만 삭제 가능
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "✅ 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요"),
        @ApiResponse(responseCode = "403", description = "❌ 삭제 권한 없음 (본인 요청 아님)"),
        @ApiResponse(responseCode = "404", description = "❌ 인증 요청을 찾을 수 없음")
    })
    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> deleteEnrollment(
            @Parameter(description = "수강 인증 요청 ID", example = "1")
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        enrollmentService.deleteEnrollment(enrollmentId, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "대기 중인 수강 인증 요청 목록 조회 (관리자 전용)",
        description = """
            👨‍💼 **관리자가 승인 대기 중인 수강 인증 요청을 조회합니다.**
            
            ### 🔍 관리자 업무
            - **증빙 자료 검토**: 학생이 제출한 성적표, 수강신청 확인서 검증
            - **수강 여부 확인**: 실제로 해당 강의를 수강했는지 판단
            - **승인/거부 결정**: 검토 결과에 따른 최종 결정
            
            ### 📋 확인 정보
            - 학생 정보
            - 강의 정보
            - 제출된 증빙 자료
            - 신청 학점 및 학기
            - 요청 일시
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 조회 성공",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요"),
        @ApiResponse(responseCode = "403", description = "❌ 관리자 권한 필요")
    })
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EnrollmentResponse>> getPendingEnrollments(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(enrollmentService.getPendingEnrollments(page, size));
    }
    
    @Operation(
        summary = "수강 인증 상태 변경 (관리자 전용)",
        description = """
            ✅❌ **관리자가 수강 인증 요청을 승인하거나 거부합니다.**
            
            ### 🔧 변경 가능한 정보
            - **승인/거부 상태**: `isCertified` 필드로 결정
            - **학점 수정**: 필요시 학점 정보 수정
            - **학기 수정**: 필요시 수강 학기 정보 수정
            - **증빙 자료**: 필요시 이미지 URL 수정
            
            ### 📌 승인 프로세스
            1. **자료 검토**: 학생이 제출한 증빙 자료 확인
            2. **정보 검증**: 학점, 학기 정보의 정확성 확인
            3. **결정**: 승인(`true`) 또는 거부(`false`)
            4. **알림**: 학생에게 결과 통보 (추후 구현)
            
            ### ⚡ 승인 효과
            - **승인**: 학생이 해당 강의에 대한 강의평 작성 가능
            - **거부**: 강의평 작성 불가, 재요청 가능
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 상태 변경 성공",
            content = @Content(schema = @Schema(implementation = EnrollmentResponse.class))),
        @ApiResponse(responseCode = "400", description = "❌ 잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요"),
        @ApiResponse(responseCode = "403", description = "❌ 관리자 권한 필요"),
        @ApiResponse(responseCode = "404", description = "❌ 인증 요청을 찾을 수 없음")
    })
    @PatchMapping("/{enrollmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnrollmentResponse> updateEnrollmentStatus(
            @Parameter(description = "수강 인증 요청 ID", example = "1")
            @PathVariable Long enrollmentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "수강 인증 상태 변경 데이터",
                content = @Content(schema = @Schema(implementation = UpdateEnrollmentRequest.class),
                    examples = {
                        @ExampleObject(name = "승인 예시", value = """
                        {
                          "isCertified": true,
                          "grade": "A+",
                          "semester": "2024-1"
                        }
                        """),
                        @ExampleObject(name = "거부 예시", value = """
                        {
                          "isCertified": false
                        }
                        """)
                    }))
            @Valid @RequestBody UpdateEnrollmentRequest request) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(enrollmentId, request));
    }
} 