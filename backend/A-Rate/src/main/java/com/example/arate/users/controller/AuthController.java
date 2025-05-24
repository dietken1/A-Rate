package com.example.arate.users.controller;

import com.example.arate.users.entity.AuthProvider;
import com.example.arate.users.entity.RefreshToken;
import com.example.arate.users.entity.User;
import com.example.arate.users.exception.OAuth2AuthenticationProcessingException;
import com.example.arate.users.exception.TokenRefreshException;
import com.example.arate.users.repository.UserRepository;
import com.example.arate.users.security.TokenProvider;
import com.example.arate.users.security.UserPrincipal;
import com.example.arate.users.service.RefreshTokenService;
import com.example.arate.users.security.oauth2.user.GoogleOAuth2UserInfo;
import com.example.arate.users.security.oauth2.user.OAuth2UserInfo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "🔐 인증/권한", description = "로그인, 토큰 관리, 사용자 정보 API")
public class AuthController {

    @Autowired
    private TokenProvider tokenProvider;
    
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Operation(
        summary = "JWT 토큰 검증 테스트",
        description = """
            🔍 **JWT 토큰이 유효한지 테스트합니다.**
            
            ### 📋 용도
            - 프론트엔드에서 토큰 유효성 확인
            - API 호출 전 인증 상태 검증
            - 디버깅 및 개발 시 토큰 테스트
            
            ### 📤 응답 정보
            - 토큰이 유효한 경우: 성공 메시지
            - 토큰이 무효한 경우: 401 Unauthorized
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 토큰 유효함",
            content = @Content(examples = @ExampleObject(value = """
            {
              "message": "토큰이 유효합니다."
            }
            """))),
        @ApiResponse(responseCode = "401", description = "❌ 토큰 무효/만료",
            content = @Content(examples = @ExampleObject(value = """
            {
              "message": "인증에 실패했습니다."
            }
            """)))
    })
    @GetMapping("/test-token")
    public ResponseEntity<String> testToken(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok("토큰이 유효합니다. 사용자: " + userPrincipal.getName());
    }

    @Operation(
        summary = "액세스 토큰 갱신",
        description = """
            🔄 **리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.**
            
            ### 🔑 토큰 관리
            - **액세스 토큰**: 짧은 유효기간 (보통 1시간)
            - **리프레시 토큰**: 긴 유효기간 (보통 2주)
            - 액세스 토큰 만료 시 리프레시 토큰으로 갱신
            
            ### 📱 사용 시나리오
            1. API 호출 시 401 에러 발생
            2. 리프레시 토큰으로 새 액세스 토큰 요청
            3. 새 토큰으로 원래 API 재호출
            4. 리프레시 토큰도 만료된 경우 재로그인 필요
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 토큰 갱신 성공",
            content = @Content(examples = @ExampleObject(name = "토큰 갱신 성공", value = """
                {
                  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                  "tokenType": "Bearer",
                  "expiresIn": 3600
                }
                """))),
        @ApiResponse(responseCode = "401", description = "❌ 리프레시 토큰 무효/만료",
            content = @Content(examples = @ExampleObject(value = """
            {
              "message": "리프레시 토큰이 유효하지 않습니다."
            }
            """)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "리프레시 토큰",
                content = @Content(examples = @ExampleObject(name = "리프레시 토큰 요청", value = """
                {
                  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                }
                """)))
            @RequestBody Object request) {
        // TODO: 실제 구현 필요
        return ResponseEntity.ok("토큰 갱신 기능 구현 필요");
    }

    @Operation(
        summary = "현재 사용자 정보 조회",
        description = """
            👤 **현재 로그인한 사용자의 정보를 조회합니다.**
            
            ### 📊 포함 정보
            - **기본 정보**: ID, 이름, 이메일, 닉네임
            - **역할 정보**: 학생/교수/관리자 구분
            - **프로필**: 프로필 이미지, 학과, 학번 등
            - **계정 상태**: 활성화 여부, 가입일
            
            ### 💡 활용 방법
            - 사용자 프로필 페이지 표시
            - 권한별 UI 분기 처리
            - 사용자 설정 페이지 정보 표시
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 조회 성공",
            content = @Content(examples = @ExampleObject(name = "사용자 정보 예시", value = """
                {
                  "id": 1,
                  "name": "홍길동",
                  "email": "hong@example.com",
                  "nickname": "길동이",
                  "role": "STUDENT",
                  "department": "컴퓨터공학과",
                  "studentNumber": "2021001234",
                  "profileImage": "https://example.com/profiles/1.jpg",
                  "createdAt": "2024-01-15T10:30:00",
                  "isActive": true
                }
                """))),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요",
            content = @Content(examples = @ExampleObject(value = """
            {
              "message": "인증이 필요합니다."
            }
            """)))
    })
    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        // TODO: 실제 구현 필요
        return ResponseEntity.ok("사용자 정보 조회 기능 구현 필요");
    }

    @Operation(
        summary = "로그아웃",
        description = """
            👋 **현재 세션을 종료하고 로그아웃합니다.**
            
            ### 🔒 로그아웃 처리
            - 서버에서 리프레시 토큰 무효화
            - 클라이언트에서 토큰 저장소 삭제 필요
            - 브라우저 캐시/쿠키 정리 권장
            
            ### 📱 프론트엔드 처리 권장사항
            1. API 호출로 서버측 로그아웃
            2. 로컬스토리지/세션스토리지 토큰 삭제
            3. 로그인 페이지로 리다이렉트
            4. 전역 상태 초기화 (Redux, Zustand 등)
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 로그아웃 성공",
            content = @Content(examples = @ExampleObject(value = """
            {
              "message": "성공적으로 로그아웃되었습니다."
            }
            """))),
        @ApiResponse(responseCode = "401", description = "❌ 인증 필요")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        // TODO: 실제 구현 필요
        return ResponseEntity.ok("성공적으로 로그아웃되었습니다.");
    }

    @Operation(
        summary = "Google OAuth 로그인 URL 생성",
        description = """
            🔗 **Google OAuth 로그인을 위한 인증 URL을 생성합니다.**
            
            ### 🚀 OAuth 로그인 플로우
            1. **인증 URL 요청**: 이 API로 Google 인증 URL 획득
            2. **Google 로그인**: 사용자를 Google 로그인 페이지로 리다이렉트
            3. **권한 승인**: 사용자가 앱 권한 승인
            4. **콜백 처리**: Google이 설정된 콜백 URL로 리다이렉트
            5. **토큰 발급**: 서버에서 JWT 토큰 생성 및 클라이언트로 전달
            
            ### 🔑 필요 권한
            - **profile**: 사용자 기본 프로필 정보
            - **email**: 이메일 주소
            """,
        parameters = {
            @Parameter(name = "redirect_uri", description = "로그인 성공 후 리다이렉트할 URL", 
                      example = "http://localhost:3000/auth/callback")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 인증 URL 생성 성공",
            content = @Content(examples = @ExampleObject(name = "Google 인증 URL", value = """
                {
                  "authUrl": "https://accounts.google.com/oauth/authorize?client_id=...&redirect_uri=...&scope=profile+email",
                  "state": "random_state_string"
                }
                """))),
        @ApiResponse(responseCode = "400", description = "❌ 잘못된 리다이렉트 URL")
    })
    @GetMapping("/google/url")
    public ResponseEntity<Object> getGoogleAuthUrl(
            @RequestParam(name = "redirect_uri", required = false) String redirectUri) {
        // TODO: 실제 구현 필요
        return ResponseEntity.ok("Google OAuth URL 생성 기능 구현 필요");
    }

    @Operation(
        summary = "개발용 토큰 테스트",
        description = """
            🛠️ **개발 환경에서 토큰 없이 API 접근을 테스트합니다.**
            
            ### ⚠️ 개발 전용
            - 프로덕션 환경에서는 비활성화 권장
            - 인증 플로우 테스트 용도
            - API 구조 확인 용도
            
            ### 🚫 보안 주의사항
            - 실제 서비스에서는 제거 필요
            - 민감한 데이터 노출 방지
            - 프로덕션 배포 전 비활성화
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "✅ 테스트 성공",
            content = @Content(examples = @ExampleObject(value = """
            {
              "message": "API가 정상적으로 작동합니다.",
              "timestamp": "2024-01-15T10:30:00",
              "version": "v1.0.0"
            }
            """)))
    })
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API가 정상적으로 작동합니다.");
    }
} 