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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "인증", description = "사용자 인증 관련 API")
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

    /**
     * 테스트용 토큰 발급 API - 개발 환경에서만 사용
     */
    @GetMapping("/test-token")
    public ResponseEntity<?> getTestToken() {
        // 1. 사용자 ID 찾기 (첫 번째 사용자)
        User user = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
        
        // 2. 토큰 생성
        String token = tokenProvider.createTokenFromUserId(user.getId());
        
        // 3. 리프레시 토큰 생성 및 저장
        String refreshToken = tokenProvider.createRefreshToken();
        refreshTokenService.createRefreshToken(user.getId(), refreshToken);
        
        // 4. 토큰 반환
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("refreshToken", refreshToken);
        response.put("userId", user.getId());
        response.put("userName", user.getName());
        response.put("userEmail", user.getEmail());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "현재 사용자 정보 조회",
        description = "JWT 토큰으로 인증된 현재 사용자의 정보를 반환합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "사용자 정보 조회 성공",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "인증되지 않은 사용자",
                content = @Content
            )
        }
    )
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> response = new HashMap<>();
        
        if (userPrincipal == null) {
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }
        
        response.put("authenticated", true);
        response.put("user", Map.of(
            "id", userPrincipal.getId(),
            "name", userPrincipal.getName(),
            "email", userPrincipal.getEmail(),
            "imageUrl", userPrincipal.getAttributes().get("picture")
        ));
        
        return ResponseEntity.ok(response);
    }
    
    // ... 나머지 기존 메소드들
}