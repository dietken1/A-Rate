# A-Rate 프로젝트

## 개발 환경 설정 (Docker)

이 프로젝트는 Docker를 사용하여 개발 환경을 쉽게 설정할 수 있습니다.

### 필수 조건

- Docker 및 Docker Compose 설치
- Git

### 실행 방법

1. 레포지토리 클론:
   ```
   git clone <repository-url>
   cd A-Rate
   ```

2. Docker Compose로 실행:
   ```
   docker-compose up
   ```

3. 서비스 접근:
   - 백엔드: http://localhost:8080
   - 프론트엔드: http://localhost:3000
   - MySQL: localhost:3306 (외부 클라이언트에서 접속 가능)

### 구성 요소

- 백엔드: Spring Boot (Java 17)
- 프론트엔드: React
- 데이터베이스: MySQL 8.0

### 개발 작업

각 컨테이너는 로컬 소스 코드 디렉토리와 연결되어 있어, 로컬에서 코드를 수정하면 컨테이너 내에서 자동으로 반영됩니다.

- 백엔드 코드: `./backend/A-Rate`
- 프론트엔드 코드: `./frontend`

### 컨테이너 관리

- 모든 서비스 시작: `docker-compose up`
- 백그라운드 실행: `docker-compose up -d`
- 특정 서비스만 실행: `docker-compose up <service_name>`
- 서비스 중지: `docker-compose down`
- 볼륨 포함 모두 삭제: `docker-compose down -v`
