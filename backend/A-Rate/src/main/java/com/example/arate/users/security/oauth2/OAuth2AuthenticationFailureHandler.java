package com.example.arate.users.security.oauth2;

import com.example.arate.users.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String redirectUri;
        
        // 쿠키에서 리디렉션 환경 파라미터 읽기
        String redirectEnvironment = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_ENVIRONMENT_PARAM_COOKIE_NAME)
                .map(cookie -> cookie.getValue())
                .orElse(null);
        
        logger.info("Auth Failure - 쿠키에서 읽은 리디렉션 환경: " + redirectEnvironment);
        
        // 요청의 출처 확인 (Origin, Referer 헤더 사용)
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String serverName = request.getServerName();
        
        logger.info("Auth Failure - 요청 정보: Origin=" + origin + ", Referer=" + referer + ", ServerName=" + serverName);
        
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
        
        // 에러 메시지를 안전하게 인코딩
        String errorMessage = exception.getLocalizedMessage();
        String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString());
        
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", encodedErrorMessage)
                .build().toUriString();

        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
} 