package com.example.arate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("🎓 A-Rate API 문서")
                        .description("""
                                ## 📚 대학 강의 평가 플랫폼 A-Rate API
                                
                                ### 🎯 주요 기능
                                - **강의 조회**: 학과, 교수별 강의 검색
                                - **강의평 작성**: 수강 인증 후 강의 평가 작성
                                - **수강 인증**: 관리자 승인을 통한 수강 확인
                                - **댓글 시스템**: 강의평에 대한 댓글 작성
                                - **역할 관리**: 학생/교수/관리자 권한 구분
                                
                                ### 🔐 인증 방식
                                - JWT Bearer Token 사용
                                - Google OAuth2 로그인 지원
                                
                                ### 📋 API 사용 가이드
                                1. **비로그인**: 강의 목록/상세 조회 가능 (첫 번째 강의평만 볼 수 있음)
                                2. **로그인**: 전체 강의평 조회, 작성, 수정, 삭제 가능
                                3. **수강 인증**: 강의평 작성 전 관리자 승인 필요
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("A-Rate Team")
                                .email("support@a-rate.kr")
                                .url("https://a-rate.kr"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("🛠️ 로컬 개발 서버"),
                        new Server()
                                .url("https://api.a-rate.kr")
                                .description("🚀 프로덕션 서버")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("🔑 JWT 토큰을 'Bearer {token}' 형태로 Authorization 헤더에 포함")))
                .tags(List.of(
                        new Tag().name("🏠 메인 대시보드").description("메인페이지 요약 정보 (비로그인 접근 가능)"),
                        new Tag().name("🎓 강의 관리").description("강의 조회, 강의평 작성/수정/삭제"),
                        new Tag().name("📋 수강 인증").description("수강 인증 요청 및 관리자 승인"),
                        new Tag().name("💬 댓글 관리").description("강의평 댓글 작성/수정/삭제"),
                        new Tag().name("📄 자료 공유").description("강의 관련 자료 업로드/다운로드"),
                        new Tag().name("🔐 인증/권한").description("로그인, 토큰 관리, 사용자 정보")
                ));
    }
} 