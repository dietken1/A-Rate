package com.example.arate.users.security.oauth2;

import com.example.arate.users.security.TokenProvider;
import com.example.arate.users.security.UserPrincipal;
import com.example.arate.users.service.RefreshTokenService;
import com.example.arate.users.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private TokenProvider tokenProvider;
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private Environment env;
    private RefreshTokenService refreshTokenService;

    @Autowired
    OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider,
                                       HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
                                       Environment env,
                                       RefreshTokenService refreshTokenService) {
        this.tokenProvider = tokenProvider;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        this.env = env;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("응답이 이미 전송되었습니다. " + targetUrl + "로 리다이렉트할 수 없습니다.");
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String redirectUri;
        
        // 요청 파라미터 확인 및 로깅
        logger.info("요청 파라미터 확인: redirect_environment=" + request.getParameter("redirect_environment"));
        
        // 쿠키에서 리디렉션 환경 파라미터 읽기
        String redirectEnvironment = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_ENVIRONMENT_PARAM_COOKIE_NAME)
                .map(cookie -> cookie.getValue())
                .orElse(null);
        
        logger.info("Auth Success - 쿠키에서 읽은 리디렉션 환경: " + redirectEnvironment);
        
        // 요청의 출처 확인 (Origin, Referer 헤더 사용)
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String serverName = request.getServerName();
        
        logger.info("Auth Success - 요청 정보: Origin=" + origin + ", Referer=" + referer + ", ServerName=" + serverName);
        
        // 클라이언트 출처 결정
        boolean isLocalClient = "local".equals(redirectEnvironment);
        
        // redirect_environment 쿠키가 없는 경우 헤더로 판단
        if (redirectEnvironment == null) {
            // Origin 헤더로 확인
            if (origin != null && (origin.contains("localhost") || origin.contains("127.0.0.1"))) {
                isLocalClient = true;
            } 
            // Referer 헤더로 확인
            else if (referer != null && (referer.contains("localhost") || referer.contains("127.0.0.1"))) {
                isLocalClient = true;
            }
            // 서버 이름으로 확인
            else if (serverName != null && (serverName.equals("localhost") || serverName.contains("127.0.0.1"))) {
                isLocalClient = true;
            }
            // 요청 URL로 확인
            else {
                String requestURL = request.getRequestURL().toString();
                logger.info("요청 URL 확인: " + requestURL);
                if (requestURL.contains("localhost") || requestURL.contains("127.0.0.1")) {
                    isLocalClient = true;
                    logger.info("요청 URL에서 localhost 감지됨, 로컬 클라이언트로 처리합니다.");
                }
            }
        }
        
        logger.info("최종 isLocalClient 결정: " + isLocalClient);
        
        // 출처에 따라 리디렉션 URL 결정
        redirectUri = "http://localhost:3000/google-auth";
        logger.info("항상 로컬 URL로 리디렉션합니다: " + redirectUri);
        
        // 액세스 토큰 생성
        String token = tokenProvider.createToken(authentication);
        
        // 리프레시 토큰 생성 및 저장
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        String refreshToken = refreshTokenService.createRefreshToken(userId).getToken();

        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
} 