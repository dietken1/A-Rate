package com.example.arate.users.security;

import com.example.arate.users.entity.User;
import com.example.arate.users.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        logger.info("JWT 필터 실행: {}", requestURI);
        
        try {
            String jwt = getJwtFromRequest(request);
            logger.info("JWT 토큰 추출: {}", jwt != null ? "토큰 있음" : "토큰 없음");

            if (StringUtils.hasText(jwt)) {
                boolean isValid = tokenProvider.validateToken(jwt);
                logger.info("JWT 토큰 유효성: {}", isValid);
                
                if (isValid) {
                    Long userId = tokenProvider.getUserIdFromToken(jwt);
                    logger.info("JWT에서 추출된 사용자 ID: {}", userId);

                    User user = userRepository.findById(userId).orElse(null);
                    if (user != null) {
                        logger.info("사용자 조회 성공: {} ({})", user.getName(), user.getEmail());
                        UserPrincipal userPrincipal = UserPrincipal.create(user);
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("인증 컨텍스트 설정 완료");
                    } else {
                        logger.warn("사용자 ID {}에 해당하는 사용자를 찾을 수 없음", userId);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("JWT 인증 필터에서 오류 발생", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 