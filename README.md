# 🎓 A-Rate: 아주대학교 강의평가 시스템

A-Rate는 아주대학교 학생들을 위한 현대적이고 사용자 친화적인 강의평가 플랫폼입니다. 학생들이 수강한 강의에 대해 평가를 작성하고, 다른 학생들의 평가를 참고하여 더 나은 수강 선택을 할 수 있도록 돕습니다.

## ✨ 주요 기능

### 🔐 인증 및 사용자 관리
- **Google OAuth 2.0 로그인**: 아주대학교 이메일(@ajou.ac.kr)로만 로그인 가능
- **JWT 토큰 기반 인증**: 안전하고 확장 가능한 인증 시스템
- **사용자 프로필 관리**: 학과, 학번 등 추가 정보 설정

### 📚 강의 검색 및 조회
- **통합 검색**: 교수명과 과목명을 동시에 검색
- **고급 필터링**: 학과별, 학수구분별(전필, 전선, 교양 등) 필터링
- **페이지네이션**: 대용량 데이터 효율적 처리
- **실시간 검색**: 키워드 입력 시 즉시 결과 표시

### 📝 강의평가 시스템
- **다차원 평가**: 전달력, 전문성, 너그러움, 효율성, 인품, 난이도 6개 영역
- **상세 정보**: 과제량, 과제난이도, 시험형태, 팀프로젝트 여부
- **맛보기 시스템**: 비작성자도 첫 번째 강의평은 무료로 열람 가능
- **제한적 공개**: 강의평 작성자만 모든 평가 열람 가능

### 🎒 수강 인증 시스템
- **인증 기반 평가**: 실제 수강 증빙을 통한 신뢰성 있는 평가
- **관리자 승인**: 제출된 수강 증빙을 관리자가 검토 후 승인
- **성적 및 학기 정보**: 받은 성적과 수강 학기 기록

### 📊 대시보드 및 통계
- **인기 강의**: 평점 기준 상위/하위 강의 목록
- **최신 활동**: 최근 작성된 강의평과 공유 자료
- **개인 통계**: 사용자별 활동 현황

## 🏗️ 시스템 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   Database      │
│   (React)       │◄──►│ (Spring Boot)   │◄──►│    (MySQL)      │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 3306    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         ▲                       ▲
         │                       │
┌─────────────────┐    ┌─────────────────┐
│     Nginx       │    │  Google OAuth   │
│   Port: 80/443  │    │     Server      │
└─────────────────┘    └─────────────────┘
```

### 🛠️ 기술 스택

**Frontend**
- React 19.1.0 + TypeScript
- Tailwind CSS (스타일링)
- React Router (라우팅)
- TanStack Query (서버 상태 관리)
- Zustand (클라이언트 상태 관리)
- React Modal Sheet, Rating Stars (UI 컴포넌트)

**Backend**
- Spring Boot 3.2.3 + Java 17
- Spring Security (인증/권한)
- Spring Data JPA (ORM)
- JWT + OAuth2 (인증)
- SpringDoc OpenAPI (API 문서화)
- Gradle (빌드 도구)

**Database**
- MySQL 8.0
- JPA/Hibernate (ORM)

**Infrastructure**
- Docker & Docker Compose
- Nginx (리버스 프록시)
- SSL/TLS 지원

**AI/ML Services**
- Google Gemini API (AI 기반 기능 지원)

## 🚀 빠른 시작

### 📋 필수 조건
- Docker 및 Docker Compose
- Git
- 최신 웹 브라우저

### ⚡ 설치 및 실행

1. **레포지토리 클론**
   ```bash
   git clone <repository-url>
   cd A-Rate
   ```

2. **환경 변수 설정**
   ```bash
   # .env 파일 생성 (docker-compose.yaml 참고)
   MYSQL_ROOT_PASSWORD=arate1234
   MYSQL_DATABASE=arate_db
   GOOGLE_CLIENT_ID=your-google-client-id
   GOOGLE_CLIENT_SECRET=your-google-client-secret
   JWT_SECRET=your-jwt-secret-key
   ```

3. **Docker Compose 실행**
   ```bash
   # 전체 서비스 시작
   docker-compose up -d
   
   # 또는 편의 스크립트 사용
   ./up.sh
   ```

4. **서비스 접근**
   - 🌐 **프론트엔드**: http://localhost:3000
   - 🔧 **백엔드 API**: http://localhost:8080
   - 📖 **API 문서**: http://localhost:8080/swagger-ui/index.html
   - 🗄️ **데이터베이스**: localhost:3306

### 🛑 서비스 중지
```bash
docker-compose down

# 또는 편의 스크립트 사용
./down.sh

# 볼륨 포함 완전 삭제
docker-compose down -v
```

## 📁 프로젝트 구조

```
A-Rate/
├── 📁 frontend/                 # React 프론트엔드
│   ├── 📁 src/
│   │   ├── 📁 components/       # 재사용 가능한 컴포넌트
│   │   ├── 📁 pages/           # 페이지 컴포넌트
│   │   ├── 📁 types/           # TypeScript 타입 정의
│   │   ├── 📁 store/           # Zustand 상태 관리
│   │   ├── 📁 lib/             # API 클라이언트 등 유틸리티
│   │   └── 📄 router.tsx       # 라우팅 설정
│   ├── 📄 package.json
│   └── 📄 Dockerfile
├── 📁 backend/A-Rate/          # Spring Boot 백엔드
│   ├── 📁 src/main/java/com/example/arate/
│   │   ├── 📁 users/           # 사용자 관리 모듈
│   │   ├── 📁 lectures/        # 강의 관리 모듈
│   │   ├── 📁 enrollments/     # 수강 인증 모듈
│   │   ├── 📁 shared/          # 공유 자료 모듈
│   │   └── 📁 config/          # 설정 클래스
│   ├── 📄 build.gradle
│   └── 📄 Dockerfile
├── 📁 db/                      # 데이터베이스 설정
├── 📁 nginx/                   # Nginx 설정
├── 📄 docker-compose.yaml     # Docker Compose 설정
├── 📄 API_SPECIFICATION.md    # 상세 API 명세서
└── 📄 README.md
```

## 🔧 개발 환경

### 🔄 핫 리로드 지원
- **프론트엔드**: 코드 변경 시 자동 새로고침
- **백엔드**: Spring Boot DevTools로 자동 재시작
- **볼륨 마운트**: 로컬 소스 코드와 컨테이너 실시간 동기화

### 🧪 개발 작업흐름
```bash
# 특정 서비스만 재시작
docker-compose restart frontend
docker-compose restart backend

# 로그 확인
docker-compose logs -f frontend
docker-compose logs -f backend

# 컨테이너 접속
docker-compose exec frontend sh
docker-compose exec backend bash
```

## 📊 API 엔드포인트

### 🔐 인증 관련
- `GET /oauth2/authorization/google` - Google OAuth 시작
- `GET /api/auth/me` - 현재 사용자 정보 조회
- `POST /api/auth/refresh` - JWT 토큰 갱신

### 📚 강의 관련
- `GET /api/v1/lectures` - 강의 목록 조회 (검색/필터링)
- `GET /api/v1/lectures/{id}` - 강의 상세 정보
- `POST /api/v1/lectures/{id}/evaluations` - 강의평 작성

### 🎒 수강 인증
- `POST /api/v1/enrollments` - 수강 인증 신청
- `GET /api/v1/enrollments` - 수강 인증 목록

### 📈 대시보드
- `GET /api/v1/dashboard/summary` - 대시보드 요약 정보

> 📖 **상세한 API 명세는 [API_SPECIFICATION.md](./API_SPECIFICATION.md)를 참고하세요.**

## 🛡️ 보안 및 인증

### 🔐 OAuth 2.0 플로우
1. Google OAuth 인증 페이지로 리디렉션
2. 사용자 동의 후 인증 코드 수신
3. 백엔드에서 Google API로 사용자 정보 획득
4. JWT 액세스 토큰 & 리프레시 토큰 발급
5. 프론트엔드에서 토큰 저장 및 API 요청 시 사용

### 🛠️ JWT 토큰 구조
- **액세스 토큰**: API 요청 인증 (24시간 유효)
- **리프레시 토큰**: 액세스 토큰 갱신 (7일 유효)
- **아주대 이메일 제한**: @ajou.ac.kr 도메인만 가입 가능

## 🧪 테스트 및 품질

### API 테스트 예시
```bash
# 강의 검색 테스트
curl "http://localhost:8080/api/v1/lectures?keyword=확률&department=수학과&courseType=전필"

# 인증 테스트
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" "http://localhost:8080/api/auth/me"
```

## 🚀 배포

### 🐳 Production 배포
```bash
# 프로덕션 이미지 빌드
docker-compose -f docker-compose.prod.yml build

# 프로덕션 환경 실행
docker-compose -f docker-compose.prod.yml up -d
```

### 🌐 SSL/TLS 설정
- Let's Encrypt를 통한 자동 SSL 인증서 발급
- Nginx를 통한 HTTPS 리디렉션
- Certbot 컨테이너로 인증서 자동 갱신

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참고하세요.

## 👥 팀

- **Frontend**: React + TypeScript 기반 모던 웹 인터페이스
- **Backend**: Spring Boot 기반 RESTful API 서버
- **DevOps**: Docker 기반 컨테이너화 및 자동 배포

## 📞 지원 및 문의

- 📖 **API 문서**: http://localhost:8080/swagger-ui/index.html
- 📋 **이슈 트래킹**: GitHub Issues
- 📧 **문의**: 프로젝트 관리자에게 연락

---

<div align="center">

**🎓 아주대학교 학생들을 위한, 학생들에 의한 강의평가 플랫폼**

Made with ❤️ for Ajou University

</div>
