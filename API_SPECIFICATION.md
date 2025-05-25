# A-Rate API 명세서

## 목차
1. [사용자 관련 API](#1-사용자-관련-api)
2. [강의 관련 API](#2-강의-관련-api)
3. [강의 평가 관련 API](#3-강의-평가-관련-api)
4. [수강 인증 관련 API](#4-수강-인증-관련-api)
5. [공유 자료 관련 API](#5-공유-자료-관련-api)
6. [댓글 관련 API](#6-댓글-관련-api)
7. [공통 응답 형식](#공통-응답-형식)
8. [인증](#인증)
9. [페이지네이션](#페이지네이션)

## 1. 사용자 관련 API

### 구글 OAuth2.0 로그인 프로세스

#### 1.1 프론트엔드 OAuth 초기화
```http
GET /oauth2/authorization/google
Response: 302 Found
Location: https://accounts.google.com/o/oauth2/v2/auth?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code&scope=email profile&state={state}
```

#### 1.2 OAuth2.0 콜백 처리
```http
GET /login/oauth2/code/google
Query Parameters:
- code: string (authorization code)
- state: string (CSRF 토큰)

Response: 200 OK
{
    "token": "string",          // JWT 토큰
    "user": {
        "id": "long",
        "email": "string",      // 구글 이메일
        "name": "string",       // 구글 계정 이름
        "picture": "string",    // 프로필 이미지 URL
        "role": "string",       // STUDENT, PROFESSOR, ADMIN
        "isProfileComplete": boolean  // 프로필 완성 여부
    }
}

Error Response: 403 Forbidden
{
    "timestamp": "datetime",
    "status": 403,
    "error": "Forbidden",
    "message": "Only @ajou.ac.kr email addresses are allowed",
    "path": "/login/oauth2/code/google"
}
```

#### 1.3 추가 정보 입력 (최초 로그인 시)
```http
POST /api/v1/auth/complete-profile
Content-Type: application/json
Authorization: Bearer {token}

Request:
{
    "nickname": "string",       // 닉네임
    "department": "string",     // 학과
    "studentNumber": "string",  // 학번
    "role": "STUDENT"          // STUDENT, PROFESSOR, ADMIN
}

Response: 200 OK
{
    "id": "long",
    "email": "string",
    "nickname": "string",
    "name": "string",
    "department": "string",
    "role": "string",
    "picture": "string",
    "createdAt": "datetime"
}
```

#### 1.4 프로필 정보 수정
```http
PATCH /api/v1/auth/profile
Content-Type: application/json
Authorization: Bearer {token}

Request:
{
    "nickname": "string",       // 선택적, 닉네임
    "department": "string",     // 선택적, 학과
    "studentNumber": "string",  // 선택적, 학번
    "role": "STUDENT"          // 선택적, STUDENT, PROFESSOR, ADMIN
}

Response: 200 OK
{
    "id": "long",
    "email": "string",
    "nickname": "string",
    "name": "string",
    "department": "string",
    "role": "string",
    "picture": "string",
    "updatedAt": "datetime"
}
```

### 인증 관련 상세 설명

#### 이메일 도메인 제한
- 서비스는 `@ajou.ac.kr` 도메인의 이메일 주소만 허용합니다.
- OAuth 로그인 시 이메일 도메인을 검증하며, 허용되지 않은 도메인인 경우 403 Forbidden 응답을 반환합니다.

#### 자동 회원가입 프로세스
1. 사용자가 구글 로그인 버튼 클릭
2. 프론트엔드에서 구글 OAuth 인증 페이지로 리다이렉트
3. 사용자가 구글 계정으로 인증
4. 백엔드에서 다음 검증 수행:
   - 이메일 도메인이 `@ajou.ac.kr`인지 확인
   - 기존 사용자인지 확인
   - 신규 사용자의 경우 자동으로 회원가입 처리
5. JWT 토큰 발급 및 사용자 정보 반환

#### 인증이 필요한 API
- 메인 페이지(강의 목록 조회)를 제외한 모든 API는 인증이 필요합니다.
- 인증이 필요한 API는 `Authorization` 헤더에 JWT 토큰을 포함해야 합니다.
- 토큰이 없거나 유효하지 않은 경우 401 Unauthorized 응답을 반환합니다.

## 2. 강의 관련 API

### 강의 목록 조회
```http
GET /api/v1/lectures
Query Parameters:
- department: string (optional)
- professorId: long (optional)
- page: int (default: 0)
- size: int (default: 20)

Response: 200 OK
{
    "content": [
        {
            "id": "long",
            "title": "string",
            "professorName": "string",
            "department": "string",
            "evaluationCount": "int",
            "averageScore": "double"
        }
    ],
    "totalElements": "long",
    "totalPages": "int",
    "currentPage": "int"
}
```

### 강의 상세 조회
```http
GET /api/v1/lectures/{lectureId}

Response: 200 OK
{
    "id": "long",
    "title": "string",
    "professor": {
        "id": "long",
        "name": "string",
        "department": "string"
    },
    "department": "string",
    "evaluations": [
        {
            "id": "long",
            "content": "string",
            "scores": {
                "delivery": "int",
                "expertise": "int",
                "generosity": "int",
                "effectiveness": "int",
                "character": "int",
                "difficulty": "int"
            },
            "assignment": {
                "amount": "NONE|FEW|NORMAL|MANY",
                "difficulty": "EASY|NORMAL|HARD"
            },
            "createdAt": "datetime",
            "author": {
                "id": "long",
                "nickname": "string"
            }
        }
    ],
    "averageScores": {
        "delivery": "double",
        "expertise": "double",
        "generosity": "double",
        "effectiveness": "double",
        "character": "double",
        "difficulty": "double"
    }
}
```

## 3. 강의 평가 관련 API

### 강의 평가 작성
```http
POST /api/v1/lectures/{lectureId}/evaluations
Content-Type: application/json

Request:
{
    "content": "string",
    "deliveryScore": "int",
    "expertiseScore": "int",
    "generosityScore": "int",
    "effectivenessScore": "int",
    "characterScore": "int",
    "difficultyScore": "int",
    "assignmentAmount": "NONE|FEW|NORMAL|MANY",
    "assignmentDifficulty": "EASY|NORMAL|HARD",
    "semester": "string"
}

Response: 201 Created
{
    "id": "long",
    "content": "string",
    "scores": {
        "delivery": "int",
        "expertise": "int",
        "generosity": "int",
        "effectiveness": "int",
        "character": "int",
        "difficulty": "int"
    },
    "assignment": {
        "amount": "string",
        "difficulty": "string"
    },
    "createdAt": "datetime",
    "author": {
        "id": "long",
        "nickname": "string"
    }
}
```

### 강의 평가 수정
```http
PATCH /api/v1/lectures/{lectureId}/evaluations/{evaluationId}
Content-Type: application/json
Authorization: Bearer {token}

Request:
{
    "content": "string",                    // 선택적
    "deliveryScore": "int",                 // 선택적
    "expertiseScore": "int",                // 선택적
    "generosityScore": "int",               // 선택적
    "effectivenessScore": "int",            // 선택적
    "characterScore": "int",                // 선택적
    "difficultyScore": "int",               // 선택적
    "assignmentAmount": "NONE|FEW|NORMAL|MANY",    // 선택적
    "assignmentDifficulty": "EASY|NORMAL|HARD"     // 선택적
}

Response: 200 OK
{
    "id": "long",
    "content": "string",
    "scores": {
        "delivery": "int",
        "expertise": "int",
        "generosity": "int",
        "effectiveness": "int",
        "character": "int",
        "difficulty": "int"
    },
    "assignment": {
        "amount": "string",
        "difficulty": "string"
    },
    "updatedAt": "datetime"
}
```

## 4. 수강 인증 관련 API

### 수강 인증 요청
```http
POST /api/v1/lectures/{lectureId}/enrollments
Content-Type: multipart/form-data

Request:
- certificationImage: file
- semester: string
- grade: string

Response: 201 Created
{
    "id": "long",
    "lectureId": "long",
    "studentId": "long",
    "semester": "string",
    "grade": "string",
    "isCertified": "boolean",
    "createdAt": "datetime",
    "certifiedAt": "datetime"
}
```

## 5. 공유 자료 관련 API

### 자료 공유
```http
POST /api/v1/lectures/{lectureId}/materials
Content-Type: multipart/form-data

Request:
- title: string
- content: string
- file: file
- tags: string[] (JSON)

Response: 201 Created
{
    "id": "long",
    "title": "string",
    "content": "string",
    "fileUrl": "string",
    "tags": ["string"],
    "createdAt": "datetime",
    "uploader": {
        "id": "long",
        "nickname": "string"
    }
}
```

### 자료 목록 조회
```http
GET /api/v1/lectures/{lectureId}/materials
Query Parameters:
- page: int (default: 0)
- size: int (default: 20)
- tag: string (optional)

Response: 200 OK
{
    "content": [
        {
            "id": "long",
            "title": "string",
            "content": "string",
            "fileUrl": "string",
            "tags": ["string"],
            "createdAt": "datetime",
            "uploader": {
                "id": "long",
                "nickname": "string"
            }
        }
    ],
    "totalElements": "long",
    "totalPages": "int",
    "currentPage": "int"
}
```

## 6. 댓글 관련 API

### 댓글 작성
```http
POST /api/v1/evaluations/{evaluationId}/replies
Content-Type: application/json

Request:
{
    "content": "string"
}

Response: 201 Created
{
    "id": "long",
    "content": "string",
    "createdAt": "datetime",
    "author": {
        "id": "long",
        "nickname": "string"
    }
}
```

### 댓글 목록 조회
```http
GET /api/v1/evaluations/{evaluationId}/replies
Query Parameters:
- page: int (default: 0)
- size: int (default: 20)

Response: 200 OK
{
    "content": [
        {
            "id": "long",
            "content": "string",
            "createdAt": "datetime",
            "author": {
                "id": "long",
                "nickname": "string"
            }
        }
    ],
    "totalElements": "long",
    "totalPages": "int",
    "currentPage": "int"
}
```

## 공통 응답 형식

### 에러 응답
```http
Response: 4xx/5xx
{
    "timestamp": "datetime",
    "status": "int",
    "error": "string",
    "message": "string",
    "path": "string"
}
```

## 인증
- 모든 API는 JWT 토큰을 사용한 인증이 필요합니다 (로그인 API 제외)
- 토큰은 `Authorization` 헤더에 `Bearer {token}` 형식으로 전송됩니다

## 페이지네이션
- 목록 조회 API는 모두 페이지네이션을 지원합니다
- 기본 페이지 크기는 20입니다
- 페이지 번호는 0부터 시작합니다 