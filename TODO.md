# TODO

## 완료

### 인프라 / 설정
- [x] Gradle 멀티모듈 구조 (api, domain, core/*)
- [x] `.gitignore`
- [x] 프로필 분리 (dev / prod / test)
- [x] 기본 프로필 `dev` 설정
- [x] 운영 환경변수 `PROD_` 접두사 분리

### 도메인 엔티티
- [x] `User`
- [x] `Note`
- [x] `Block` (ProblemType enum 분리)
- [x] `Question`
- [x] `ReviewSchedule` (망각곡선 주기, stage 범위 검증)
- [x] `ReviewResult` (BaseEntity 상속)

### 서비스 레이어
- [x] `UserService` — register, findById, findByEmail
- [x] `NoteService` — create, findById, findAllByUser, updateTitle, delete
- [x] `BlockService` — create, findById, findAllByNote, update, delete
- [x] `QuestionService` — create, findById, findActiveByBlock, deactivate
- [x] `ReviewService` — createInitialSchedule, findTodaySchedules, completeReview

### 예외 처리
- [x] `BusinessException` (domain)
- [x] `NotFoundException`
- [x] `GlobalExceptionHandler`

### API 공통
- [x] `ApiResponse<T>` 래퍼

### 테스트
- [x] `UserServiceTest`
- [x] `NoteServiceTest`
- [x] `ReviewServiceTest`

---

## 기술 결정 필요

### 사용자가 문제를 직접 입력하는 케이스
사용자 피드백 수집 후 결정. 필요 시 `Question`에 `source(USER_INPUT | AI_GENERATED)` 필드 추가 검토.

### 복습 실패 시 stage 초기화 전략
현재: `DIDNT_KNOW` 시 무조건 stage 1로 초기화 (처음부터 재시작)

고려할 대안:
- **완전 초기화 (현재)** — stage 1로 리셋. 단순하지만 stage 4~5에서 실패하면 너무 가혹함
- **한 단계 감소** — stage - 1로 감소. 점진적이지만 반복 실패 시 수렴 안 됨
- **고정 패널티** — 실패 시 무조건 stage 2로 (1단계는 건너뜀)
- **실패 횟수 기반** — 연속 실패 N회 시에만 stage 1로, 1회 실패는 stage - 1

결정 전 고려사항:
- UX: 사용자가 좌절하지 않으면서도 복습 효과를 보장할 수 있는가
- `ReviewResult` 이력을 활용해 연속 실패 횟수를 집계할 수 있음

---

## 미완료

### REST 컨트롤러
- [ ] `UserController` — 회원가입, 로그인
- [ ] `NoteController` — 노트 CRUD
- [ ] `BlockController` — 블럭 CRUD
- [ ] `QuestionController` — 문제 조회, 비활성화
- [ ] `ReviewController` — 오늘 복습 조회, 복습 완료 처리

### 테스트
- [ ] Repository 슬라이스 테스트 (`@DataJpaTest`)
- [ ] Controller 슬라이스 테스트 (`@WebMvcTest`)

### core-auth
- [ ] Spring Security 설정
- [ ] JWT 발급 / 검증
- [ ] 로그인 API

### core-ai
- [ ] Claude API 연동
- [ ] 블럭 내용 기반 문제 자동 생성

### core-mail
- [ ] 이메일 발송 설정
- [ ] 복습 알림 이메일

### core-redis
- [ ] Redis 설정
- [ ] 캐시 적용
