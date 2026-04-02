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
