# 🎓 A-Rate: 아주대 학생들을 위한 강의평가 시스템
<br/>

> 💻 개발 기간 | 2025.05.24 ~ 2025.05.25

<p align="center">
  <img width="822" height="462" alt="a-rate-main" 
       src="https://github.com/user-attachments/assets/d4841981-029e-4d39-885c-f01e08bf30c9" />
</p>

---
<br/>

## 💎 서비스 소개

> **신뢰할 수 있는 강의 평가와 학점 관리를 한 번에**
> 
**A-Rate는 아주대학교 학생들을 위한 강의 평가 및 학점 관리 플랫폼입니다.** 기존 에브리타임 강의평 기능의 정보 비대칭성과 신뢰성 문제를 해결하고, 더 나아가 수강신청과 학점 관리에 실질적인 도움을 주는 것을 목표로 합니다.

<br/>

<p align="center">
  <img width="822" alt="시간표 등록 및 인증" src="https://github.com/user-attachments/assets/d7232af3-5344-4e94-819e-bf8326b64cf3" />
  <br/>
  <strong>수강생 인증을 위한 시간표 등록 화면입니다. 실제 수강생만 강의를 평가할 수 있도록 하여 평가의 신뢰도를 높였습니다.</strong>
  <br/>
  <small>이전에는 수강하지 않은 학생도 평가를 남길 수 있었던 허점을 개선한 A-Rate의 핵심 기능입니다.</small>
</p>
<br/>

<p align="center">
  <img width="822" alt="강의 평가 진행" src="https://github.com/user-attachments/assets/88d599b3-5180-4ac6-806f-22085f43aeb0" />
  <br/>
  <strong>시간표 인증이 완료된 학생은 자신이 수강한 강의에 대한 평가를 진행할 수 있습니다.</strong>
</p>
<br/>

<p align="center">
  <img width="822" alt="교수 평가 현황 대시보드" src="https://github.com/user-attachments/assets/ad317450-61d5-4f9b-a49e-c968a7814152" />
  <br/>
  <strong>교수 계정에서는 자신의 강의에 대한 평가 현황을 차트가 포함된 대시보드 형태로 상세히 확인할 수 있습니다.</strong>
</p>
<br/>

## 👨‍💻 팀원 소개

<br/>

<table align="center">
  <tr>
    <td align="center" width="25%">
      <img width="460" height="460" alt="Image" src="https://github.com/user-attachments/assets/057399e3-32c3-43b1-8104-c9d1ee0e694e" /><br/>
      <b>정원준</b><br/>
      <code>Backend Developer</code><br/>
      <a href="https://github.com/dietken1">GitHub</a>
    </td>
    <td align="center" width="25%">
      <img width="460" height="460" alt="member2" src="https://github.com/user-attachments/assets/7a7c0621-e0f8-4302-89f5-108bb2a5c956" /><br/>
      <b>전민규</b><br/>
      <code>Backend Developer</code><br/>
      <a href="https://github.com/Mango-Juice">GitHub</a>
    </td>
    <td align="center" width="25%">
      <img width="460" height="460" alt="member3" src="https://github.com/user-attachments/assets/fc48717c-fd1b-4d4d-8202-92a774b4ea5a" /><br/>
      <b>문경호</b><br/>
      <code>Frontend Developer</code><br/>
      <a href="https://github.com/ansrudgh">GitHub</a>
    </td>
    <td align="center" width="25%">
      <img width="460" height="460" alt="member4" src="https://github.com/user-attachments/assets/427b30fb-8018-4163-936b-f6590159601c" /><br/>
      <b>이예림</b><br/>
      <code>Designer</code><br/>
      <a href="https://github.com/yelim8902">GitHub</a>
    </td>
  </tr>
</table>
<br/>

## ⚙️ 기술 스택
### ✤ Tech Stack
| 👀 Category | ⚒️ Tech | 📝 Description |
|:----------:|:------:|:-------------:|
| **`Backend`** | Spring Boot, Java, Gradle | 핵심 비즈니스 로직 및 API 서버 구현 |
| **`Frontend`** | React, TypeScript, Tailwind CSS | 사용자 인터페이스 및 경험(UI/UX) 개발 |
| **`Web / Reverse Proxy`** | Nginx | 요청 라우팅 및 리버스 프록시 |
| **`Database`** | MySQL | 영속 데이터(강의, 평가, 유저 정보) 저장 |
| **`Statistics`** | Python, Jupyter Notebook | 강의 데이터 분석 및 통계 처리 |
| **`Infrastructure`** | Docker, Docker Compose | 컨테이너화 및 서비스 환경 구성 |
| **`Collaboration`** | GitHub, Notion, Swagger, Figma | 협업, 문서화 및 API 명세 관리 |

<br/>

## 📊 ERD
<br/>
<p align="center">
<img width="1122" height="673" alt="image" src="https://github.com/user-attachments/assets/d161f66f-1ea6-4d52-aebb-1b78642ff8c3" />
</p>
<br/>

## 📏 개발 컨벤션
### Git
**✤ `Git Workflow`**
- **`Issue`** → **`Branch`** → **`PR`** → **`Review`** → **`Merge`** 의 흐름을 준수합니다.
- `main` 또는 `develop` 브랜치에 직접 커밋하지 않습니다.
- Pull Request는 **Squash and Merge** 방식을 사용합니다.
<br/>

**✤ `Branch & Commit`**
- **Branch**: `<prefix>/<issue-number>` (e.g., `feat/12`, `fix/31`)
- **Commit Message**: `[#이슈번호] <prefix>(스코프): 설명` (e.g., `[#1] feat(User): 로그인 API 구현`)
  <br/>

**✤ `Pull Request`**
- PR 제목: `[prefix] #이슈번호 - 설명`
- 코드 리뷰는 최소 1명 이상의 승인을 받아야 합니다.

---

### Code
**✤ `네이밍 규칙`**
- **Class**: PascalCase  
- **Method / Variable**: camelCase  
- **DB Table**: snake_case  
- **Constant**: UPPER_SNAKE_CASE  

**✤ `메서드 네이밍`**
- 조회: `find...()`, `get...()`
- 생성: `create...()`, `save...()`
- 수정: `update...()`
- 삭제: `delete...()`

**✤ `DTO 네이밍`**
- **Request**: `...Request` (e.g., `CreateEnrollmentRequest`)
- **Response**: `...Response` (e.g., `LectureDetailResponse`)
  
<br/>

## 🌳 백엔드 패키지 구조
```
com.example.arate
├── common
│   ├── controller
│   └── exception
├── config
├── enrollments
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── exception
│   ├── repository
│   └── service
├── lectures
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── exception
│   ├── repository
│   └── service
├── materials
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── exception
│   ├── repository
│   └── service
├── professors
│   ├── entity
│   └── repository
├── replies
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── exception
│   ├── repository
│   └── service
└── users
    ├── controller
    ├── entity
    ├── exception
    ├── repository
    ├── security
    ├── service
    └── util
```
