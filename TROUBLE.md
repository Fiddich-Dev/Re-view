# TROUBLE.md

개발 중 발생한 문제와 해결 방법을 기록합니다.

---

## [2026-04-02] Gradle 멀티모듈 의존성 전달 문제

### 증상
`core-auth` 모듈에서 `domain`의 `RefreshTokenRepository`(JpaRepository 상속)를 사용할 때 컴파일 에러 발생:
```
class file for org.springframework.data.jpa.repository.JpaRepository not found
cannot find symbol: method save(RefreshToken)
```

### 원인
Gradle의 `implementation` scope는 해당 모듈 내부에서만 사용 가능하고, 의존하는 다른 모듈에는 전달되지 않는다.

```
# implementation일 때 (문제)
core-auth → domain → JPA (❌ core-auth에서 JPA 클래스 안 보임)

# api일 때 (해결)
core-auth → domain → JPA (✅ core-auth에서도 JPA 클래스 접근 가능)
```

`domain`의 Repository 인터페이스가 `JpaRepository<T, ID>`를 extends하고 있으므로,
`domain`을 사용하는 모든 모듈에서 JPA 클래스가 필요하다.
이처럼 의존성이 모듈의 공개 API(인터페이스 시그니처)에 노출되는 경우 `api` scope를 사용해야 한다.

### 해결
`domain/build.gradle`에서 JPA 의존성을 `implementation` → `api`로 변경:
```gradle
// 변경 전
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

// 변경 후
api 'org.springframework.boot:spring-boot-starter-data-jpa'
```

`api` scope 사용을 위해 루트 `build.gradle`의 subprojects에 `java-library` 플러그인이 적용되어 있어야 한다 (이미 적용됨).

### 교훈
- 모듈의 **공개 API에 타입이 노출**되는 의존성은 `api`를 사용
- 모듈 **내부 구현에서만** 사용하는 의존성은 `implementation`을 사용
- `domain`처럼 Repository 인터페이스를 제공하는 모듈은 JPA를 `api`로 선언해야 하위 모듈에서 사용 가능
