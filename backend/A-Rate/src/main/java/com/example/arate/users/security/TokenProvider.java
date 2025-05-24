package com.example.arate.users.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import java.util.Base64;

@Service
public class TokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpirationMs;

    private Key getSigningKey() {
        // HS512 알고리즘을 위한 안전한 키 생성 (최소 64바이트/512비트 필요)
        try {
            // 기존 시크릿을 사용하되, 안전한 길이로 만들기
            byte[] keyBytes = jwtSecret.getBytes();
            // Base64로 인코딩하여 키의 길이 늘리기
            String encodedKey = Base64.getEncoder().encodeToString(keyBytes);
            // 최소 64바이트가 되도록 반복
            while (encodedKey.getBytes().length < 64) {
                encodedKey = encodedKey + encodedKey;
            }
            // 64바이트로 제한
            encodedKey = encodedKey.substring(0, 64);
            
            return Keys.hmacShaKeyFor(encodedKey.getBytes());
        } catch (Exception e) {
            // 예외 발생 시 기본적으로 안전한 키 생성
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String createTokenFromUserId(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String createRefreshToken() {
        return UUID.randomUUID().toString();
    }
    
    public Instant getRefreshTokenExpiryDate() {
        return Instant.now().plusMillis(refreshTokenExpirationMs);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
} 