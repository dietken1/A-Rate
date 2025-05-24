package com.example.arate.users.service;

import com.example.arate.users.entity.RefreshToken;
import com.example.arate.users.entity.User;
import com.example.arate.users.exception.TokenRefreshException;
import com.example.arate.users.repository.RefreshTokenRepository;
import com.example.arate.users.repository.UserRepository;
import com.example.arate.users.security.TokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RefreshTokenService {
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TokenProvider tokenProvider;
    
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
        
        // 사용자에게 기존 리프레시 토큰이 있으면 삭제
        refreshTokenRepository.findByUser(user)
                .ifPresent(token -> refreshTokenRepository.delete(token));
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenProvider.createRefreshToken())
                .expiryDate(tokenProvider.getRefreshTokenExpiryDate())
                .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
        }
        
        return token;
    }
    
    @Transactional
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
        
        refreshTokenRepository.deleteByUser(user);
    }
} 