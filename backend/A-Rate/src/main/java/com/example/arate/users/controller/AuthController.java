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
    
    @Operation(
        summary = "액세스 토큰 갱신",
        description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "토큰 갱신 성공",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "리프레시 토큰이 유효하지 않음",
                content = @Content
            )
        }
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = tokenProvider.createTokenFromUserId(user.getId());
                    
                    return ResponseEntity.ok(Map.of(
                        "accessToken", token,
                        "refreshToken", requestRefreshToken
                    ));
                })
                .orElseThrow(() -> new TokenRefreshException("리프레시 토큰이 데이터베이스에 존재하지 않습니다!"));
    }
    
    @Operation(
        summary = "인증 API 테스트",
        description = "인증 API가 정상적으로 작동하는지 테스트합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "API 정상 작동 중",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        return ResponseEntity.ok(Map.of("message", "인증 API가 정상적으로 작동 중입니다."));
    }
    
    @Operation(
        summary = "로그아웃",
        description = "사용자의 리프레시 토큰을 삭제하여 로그아웃 처리합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "로그아웃 성공",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "인증되지 않은 사용자",
                content = @Content
            )
        }
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        refreshTokenService.deleteByUserId(userId);
        
        return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다."));
    }
    
    @Operation(
        summary = "구글 OAuth 콜백 처리",
        description = "구글 인증 후 반환된 코드를 처리하여 액세스 토큰과 리프레시 토큰을 생성합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "토큰 생성 성공",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청",
                content = @Content
            )
        }
    )
    @PostMapping("/google/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestBody GoogleCallbackRequest request) {
        try {
            // 1. 구글 인증 코드로 토큰 교환
            ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
            
            // 구글 토큰 엔드포인트 URL 구성
            String tokenEndpoint = googleRegistration.getProviderDetails().getTokenUri();
            
            // 토큰 요청 파라미터 구성
            Map<String, String> params = new HashMap<>();
            params.put("code", request.getCode());
            params.put("client_id", googleClientId);
            params.put("client_secret", googleClientSecret);
            params.put("redirect_uri", request.getRedirectUri());
            params.put("grant_type", "authorization_code");
            
            // 구글 API에 토큰 요청
            RestTemplate restTemplate = new RestTemplate();
            GoogleTokenResponse googleTokenResponse = restTemplate.postForObject(
                tokenEndpoint, 
                params, 
                GoogleTokenResponse.class
            );
            
            // 2. 액세스 토큰으로 사용자 정보 요청
            String userInfoEndpoint = googleRegistration.getProviderDetails().getUserInfoEndpoint().getUri();
            
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + googleTokenResponse.getAccess_token());
            
            GoogleUserInfo googleUserInfo = restTemplate.getForObject(
                userInfoEndpoint + "?access_token=" + googleTokenResponse.getAccess_token(),
                GoogleUserInfo.class
            );
            
            // 3. 이메일 검증
            if (!StringUtils.hasText(googleUserInfo.getEmail())) {
                throw new OAuth2AuthenticationProcessingException("이메일을 찾을 수 없습니다.");
            }
            
            // 아주대학교 이메일 검증
            if (!googleUserInfo.getEmail().endsWith("@ajou.ac.kr")) {
                throw new OAuth2AuthenticationProcessingException("아주대학교 계정(@ajou.ac.kr)으로만 로그인이 가능합니다.");
            }
            
            // 4. 사용자 정보로 User 객체 생성 또는 업데이트
            Optional<User> userOptional = userRepository.findByEmail(googleUserInfo.getEmail());
            User user;
            
            if (userOptional.isPresent()) {
                user = userOptional.get();
                if (!user.getProvider().equals(AuthProvider.GOOGLE)) {
                    throw new OAuth2AuthenticationProcessingException("다른 계정으로 이미 가입되어 있습니다.");
                }
                
                user.setName(googleUserInfo.getName());
                user.setImageUrl(googleUserInfo.getPicture());
                user = userRepository.save(user);
            } else {
                user = User.builder()
                    .name(googleUserInfo.getName())
                    .email(googleUserInfo.getEmail())
                    .imageUrl(googleUserInfo.getPicture())
                    .provider(AuthProvider.GOOGLE)
                    .providerId(googleUserInfo.getSub())
                    .emailVerified(true)
                    .build();
                
                user = userRepository.save(user);
            }
            
            // 5. JWT 토큰 및 리프레시 토큰 생성
            String token = tokenProvider.createTokenFromUserId(user.getId());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
            
            // 6. 응답 반환
            return ResponseEntity.ok(Map.of(
                "token", token,
                "refreshToken", refreshToken.getToken(),
                "user", Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "imageUrl", user.getImageUrl()
                )
            ));
            
        } catch (OAuth2AuthenticationProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "인증 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @Schema(description = "토큰 갱신 요청")
    public static class TokenRefreshRequest {
        @Schema(description = "리프레시 토큰", example = "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6")
        private String refreshToken;
        
        public String getRefreshToken() {
            return refreshToken;
        }
        
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
    
    @Schema(description = "구글 콜백 요청")
    public static class GoogleCallbackRequest {
        @Schema(description = "구글 인증 코드", example = "4/0AY0e-g6...")
        private String code;
        
        @Schema(description = "리디렉션 URI", example = "http://localhost:3000/google-auth")
        private String redirectUri;
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getRedirectUri() {
            return redirectUri;
        }
        
        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }
    }
    
    public static class GoogleTokenResponse {
        private String access_token;
        private String expires_in;
        private String id_token;
        private String scope;
        private String token_type;
        
        public String getAccess_token() {
            return access_token;
        }
        
        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }
        
        public String getExpires_in() {
            return expires_in;
        }
        
        public void setExpires_in(String expires_in) {
            this.expires_in = expires_in;
        }
        
        public String getId_token() {
            return id_token;
        }
        
        public void setId_token(String id_token) {
            this.id_token = id_token;
        }
        
        public String getScope() {
            return scope;
        }
        
        public void setScope(String scope) {
            this.scope = scope;
        }
        
        public String getToken_type() {
            return token_type;
        }
        
        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }
    }
    
    public static class GoogleUserInfo {
        private String sub;
        private String name;
        private String email;
        private String picture;
        
        public String getSub() {
            return sub;
        }
        
        public void setSub(String sub) {
            this.sub = sub;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPicture() {
            return picture;
        }
        
        public void setPicture(String picture) {
            this.picture = picture;
        }
    }
} 