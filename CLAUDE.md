# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 작업할 때 참고하는 가이드입니다.

## 절대 규칙

> 어떤 상황에서도 위반 금지

- **운영 DB 직접 쿼리 금지** — `prod` 프로필 환경의 데이터베이스에 SELECT를 포함한 모든 쿼리 실행 금지
- **`.env` 파일 커밋 금지** — 환경 변수 파일을 git에 추가하거나 커밋하지 않음

## 프로젝트 개요

**Re:view**는 Spring Boot 기반 웹 애플리케이션입니다. 현재 초기 개발 단계로, 기본 스캐폴드만 존재합니다.

- Group: `com.fiddich`
- 기본 패키지: `com.fiddich.Re.view`

### 기술 스택

| 분류 | 기술 |
|------|------|
| 백엔드 | Spring Boot 4.0.5 (Java 17) |
| 데이터베이스 | MySQL (prod/dev), H2 (test) |
| 캐시 | Redis |
| AI | Claude API (문제 자동 생성) |
| CI/CD | GitHub Actions |
| 컨테이너 | Docker |


## 명령어

```bash
# 빌드
./gradlew build

# 애플리케이션 실행 (기본 포트 8080)
./gradlew bootRun

# 전체 테스트 실행
./gradlew test

# 단일 테스트 클래스 실행
./gradlew test --tests "com.fiddich.Re.view.ReViewApplicationTests"

# 빌드 결과물 정리
./gradlew clean
```

## 아키텍처

Gradle 멀티모듈 구조로 구성됩니다:

```
Re-view/
├── api/              # REST 컨트롤러, 요청/응답 DTO, Spring Boot 진입점
├── core/
│   ├── core-redis/   # Redis 설정, 캐시 유틸
│   ├── core-auth/    # 로그인, JWT, Spring Security
│   ├── core-ai/      # Claude API 연동, 문제 자동 생성
│   └── core-mail/    # 이메일 알림 (웹 복습 알림)
├── domain/           # 엔티티, 서비스, Repository (QueryDSL 포함)
└── build.gradle      # 루트 빌드
```

**모듈 의존성 방향:**
- `api` → `core/*`, `domain`
- `core/*` → `domain` (필요 시)
- `domain` → 의존성 없음 (순수 도메인)

## 프로필 및 데이터베이스

YAML 설정 파일은 3개 프로필로 분리 관리합니다:

| 프로필 | 환경 | 데이터베이스 |
|--------|------|-------------|
| `prod` | 운영 | MySQL |
| `dev` | 개발 | MySQL |
| `test` | 테스트 | H2 (인메모리) |

설정 파일 구조:
```
resources/
├── application.yaml          # 공통 설정 (spring.profiles.active 지정)
├── application-prod.yaml     # 운영 환경
├── application-dev.yaml      # 개발 환경
└── application-test.yaml     # 테스트 환경
```

프로필 지정 실행:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 코딩 컨벤션

### 네이밍

| 대상 | 규칙 | 예시 |
|------|------|------|
| 클래스 | PascalCase | `UserService`, `ReviewController` |
| 메서드/변수 | camelCase | `findUserById` |
| 상수 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| DB 테이블/컬럼 | snake_case | `user_reviews` |
| API URL | kebab-case + `/api/v1/` prefix | `/api/v1/user-reviews` |

### 패턴

- **Service** — 인터페이스 분리 없이 구현 클래스만 작성 (`UserService.java`)
- **DTO** — 요청/응답 분리 (`CreateUserRequest`, `UserResponse`)
- **공통 응답** — 모든 API 응답은 `ApiResponse<T>` 래퍼로 통일
- **Repository 인터페이스** — `domain` 모듈에 선언, 구현체는 `infra` 모듈에 위치
- **예외** — 커스텀 예외 클래스 사용, `@ControllerAdvice`로 일괄 처리

### TDD 사이클

모든 기능은 테스트 먼저 작성한다:

1. **Red** — 실패하는 테스트 먼저 작성
2. **Green** — 테스트를 통과하는 최소한의 구현
3. **Refactor** — 중복 제거 및 가독성 개선

**레이어별 테스트 전략:**

| 레이어 | 방식 | 어노테이션 |
|--------|------|-----------|
| Service | Mockito 단위 테스트 | `@ExtendWith(MockitoExtension.class)` |
| Repository | H2 슬라이스 테스트 | `@DataJpaTest` |
| Controller | MockMvc 슬라이스 테스트 | `@WebMvcTest` |
| 통합 | 전체 컨텍스트 | `@SpringBootTest` + `@ActiveProfiles("test")` |

```bash
# 특정 모듈 테스트만 실행
./gradlew :domain:test
./gradlew :api:test
```

### 커밋 메시지

[Conventional Commits](https://www.conventionalcommits.org/) 형식 사용, 한글 작성:

```
feat: 로그인 API 추가
fix: 토큰 만료 처리 버그 수정
refactor: UserService 메서드 분리
test: UserService 단위 테스트 추가
chore: 의존성 업데이트
docs: API 명세 업데이트
```

## 모듈별 패키지 구조

기본 패키지: `com.fiddich.review`

**`api` 모듈** — 진입점, 요청/응답 처리
```
api/src/main/java/com/fiddich/review/
├── controller/        # REST 컨트롤러
├── dto/
│   ├── request/       # 요청 DTO
│   └── response/      # 응답 DTO
└── common/            # ApiResponse<T>, GlobalExceptionHandler
```

**`domain` 모듈** — 비즈니스 로직, 도메인별로 패키지 구성
```
domain/src/main/java/com/fiddich/review/
└── {domain}/          # 예: user, review
    ├── {Domain}.java                   # 엔티티
    ├── {Domain}Service.java            # 서비스
    ├── {Domain}Repository.java         # JpaRepository<T, ID> extends
    ├── {Domain}RepositoryCustom.java   # QueryDSL 메서드 인터페이스
    └── {Domain}RepositoryImpl.java     # QueryDSL 구현체
```

동적 쿼리는 QueryDSL 사용. `{Domain}Repository`가 `{Domain}RepositoryCustom`을 함께 extends하면 Spring Data JPA가 `{Domain}RepositoryImpl`을 자동으로 연결.

**`core` 서브모듈** — 기능별 독립 모듈로 구성
```
core/core-redis/src/main/java/com/fiddich/review/redis/
core/core-auth/src/main/java/com/fiddich/review/auth/
core/core-ai/src/main/java/com/fiddich/review/ai/
core/core-mail/src/main/java/com/fiddich/review/mail/
```

## 도메인 컨텍스트

**핵심 플로우**
```
노트 작성 → AI 문제 생성 → 복습 스케줄링 → 알림 → 복습 → 피드백 → 주기 재조정
```

**주요 도메인**

| 도메인 | 설명 |
|--------|------|
| `Note` | 학습 노트 (제목 + 블럭 목록) |
| `Block` | 노트의 구성 단위. 텍스트 또는 이미지, 선택적으로 관련 문제 포함 |
| `Question` | AI가 Block 내용을 기반으로 자동 생성한 복습 문제 |
| `ReviewSchedule` | 망각곡선 기반 복습 주기 스케줄 (1→3→7→21→30일) |
| `ReviewResult` | 복습 결과 (알았음/몰랐음). 결과에 따라 다음 복습 주기 조정 |

**알림 방식**
- 웹: 이메일 (`core-mail`)
- 앱: 푸시 알림

## 주요 파일

| 파일 | 설명 |
|------|---------|
| `src/main/java/com/fiddich/Re/view/ReViewApplication.java` | Spring Boot 진입점 |
| `src/main/resources/application.yaml` | 애플리케이션 설정 |
| `build.gradle` | Gradle 빌드 — 의존성 및 플러그인 |
