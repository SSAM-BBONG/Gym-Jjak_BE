# 🌐 Global 공통 컴포넌트 안내

`global` 패키지는 특정 비즈니스 도메인에 종속되지 않는 공통 기반 기능을 제공합니다. 각 도메인은 여기의 응답 형식·예외 계층·보안 인증·관측 규칙을 재사용하며, 개별 도메인 규칙 자체는 `global`에 두지 않습니다.

## 1. 패키지 책임 지도

```text
global
├─ application      : 공통 인증 UseCase, 보존 정책 Job 계약
├─ domain           : 공통 예외·오류 코드·인증 Claim·도메인 이벤트·파일 타입
├─ infrastructure   : Spring 설정, JWT/OAuth2, AOP, 캐시, Trace, 스케줄러, JPA 기반 엔티티
├─ presentation     : 표준 성공·실패 응답, 전역 예외 처리, HTTP JWT 인증 필터
└─ support          : 현재 구현체는 없으며, 향후 공통 보조 기능을 둘 수 있는 예약 패키지
```

## 2. 표준 API 응답

### 성공 응답: `GlobalApiResponse<T>`

모든 Controller는 가능한 한 `GlobalApiResponse<T>`로 성공 응답을 감쌉니다.

```json
{
  "status": 200,
  "code": "DOMAIN_200_1",
  "message": "도메인별 성공 메시지입니다.",
  "data": {}
}
```

| 구성 요소 | 역할 |
| --- | --- |
| `status` | 실제 HTTP 상태 코드입니다. |
| `code` | 각 도메인의 `ResponseCode` enum이 제공하는 서비스 코드입니다. |
| `message` | 호출자에게 전달할 성공 메시지입니다. |
| `data` | API별 응답 DTO입니다. 값이 없으면 `null`입니다. |
| `ok(...)` | `200 OK` 응답을 조립합니다. |
| `created(...)` | `201 Created` 응답을 조립합니다. |

### 실패 응답: `GlobalApiErrorResponse`

```json
{
  "timestamp": "2026-07-22T10:00:00",
  "status": 400,
  "code": "COMMON_400",
  "message": "잘못된 요청입니다.",
  "traceId": "a1b2c3d4",
  "details": {
    "errors": [
      {
        "field": "name",
        "reason": "이름은 필수입니다."
      }
    ]
  }
}
```

- `traceId`는 `TraceIdFilter`가 생성하고 응답 Header `X-Trace-Id`에도 추가합니다.
- `details`에는 검증 오류 필드 목록 또는 도메인 예외의 context가 포함될 수 있습니다.
- 예상하지 못한 예외는 내부 원인을 응답에 노출하지 않고 `COMMON_500`으로 처리합니다. 🔒

## 3. 예외 계층과 전역 처리

```text
도메인 또는 Adapter에서 예외 발생
→ ApplicationException 하위 예외로 변환
→ GlobalExceptionHandler
→ ErrorCode의 HTTP 상태·code·message로 GlobalApiErrorResponse 생성
→ traceId와 details를 포함해 응답
```

### 예외 계층

| 클래스 | 사용 위치 | 의미 |
| --- | --- | --- |
| `ApplicationException` | 모든 커스텀 예외의 부모 | `ErrorCode`, 원인 예외, context를 보관합니다. |
| `BusinessException` | 도메인 규칙 위반 | 중복, 상태 전이 불가, 권한 정책 위반 등에 사용합니다. |
| `InfrastructureException` | 기술 연동 실패 | 외부 API, S3, DB 등 Adapter 계층의 기술 예외를 변환할 때 사용합니다. |
| `BadRequestException` | 400 계열 | 잘못된 입력 또는 도메인 요청값 오류에 사용합니다. |
| `UnauthorizedException` / `ForbiddenException` | 401·403 계열 | 인증 또는 권한 오류에 사용합니다. |
| `NotFoundException` / `ConflictException` | 404·409 계열 | 리소스 부재 또는 현재 상태 충돌에 사용합니다. |

### `GlobalExceptionHandler`가 처리하는 주요 상황

| 예외 | 응답 코드 | 처리 내용 |
| --- | --- | --- |
| `ApplicationException` | 각 예외의 `ErrorCode` | 도메인·인프라 예외를 표준 오류 응답으로 변환합니다. |
| `MethodArgumentNotValidException` | `COMMON_400` | `@RequestBody @Valid` 오류를 필드별 `details.errors`로 반환합니다. |
| `BindException` | `COMMON_400` | `@ModelAttribute`·Query/Form 바인딩 검증 오류를 반환합니다. |
| `ConstraintViolationException` | `COMMON_400` | `@RequestParam`, `@PathVariable` 검증 오류를 반환합니다. |
| `MethodArgumentTypeMismatchException` | `COMMON_400` | 날짜·숫자 등 파라미터 타입 변환 실패를 반환합니다. |
| `DataIntegrityViolationException` | `COMMON_409` | DB 제약 조건 위반을 충돌로 처리합니다. |
| `AuthorizationDeniedException` | `COMMON_403` | `@PreAuthorize` 권한 거부를 처리합니다. |
| 그 외 `Exception` | `COMMON_500` | 상세 예외는 로그에 남기고 일반 메시지만 반환합니다. |

## 4. HTTP 보안과 JWT 인증

### 요청 인증 흐름

```text
HTTP Request
→ TraceIdFilter: traceId 생성·MDC 저장·X-Trace-Id 응답 Header 설정
→ JwtAuthenticationFilter
→ Authorization: Bearer {accessToken} 우선 확인
→ 없으면 accessToken Cookie 확인
→ AuthenticateAccessTokenUseCase
→ JwtTokenPort / JwtTokenAdapter / JwtTokenProvider
→ JwtClaims(userId, username, role) 검증·파싱
→ JwtAuthenticationConverter
→ AuthUser를 principal로 한 Authentication 생성
→ SecurityContextHolder 저장
→ SecurityConfig URL 권한 및 Controller @PreAuthorize 검사
```

| 컴포넌트 | 역할 |
| --- | --- |
| `SecurityConfig` | Stateless JWT 보안, CORS, URL별 역할 권한, 인증·인가 실패 Handler를 설정합니다. |
| `JwtAuthenticationFilter` | 요청마다 토큰을 추출·검증하고 성공 시 `SecurityContext`에 인증 정보를 저장합니다. |
| `JwtAuthenticationConverter` | `JwtClaims`를 `AuthUser` principal과 Spring Security 권한으로 변환합니다. |
| `JwtTokenAdapter` | Application 계층의 `JwtTokenPort`를 JWT 구현체에 연결합니다. |
| `CustomAuthenticationEntryPoint` | 인증되지 않은 요청의 표준 401 오류 응답을 생성합니다. |
| `CustomAccessDeniedHandler` | URL 권한이 부족한 요청의 표준 403 오류 응답을 생성합니다. |
| `OAuth2SuccessHandler` | OAuth2 로그인 성공 후 프로젝트의 토큰 발급·리다이렉트 흐름을 처리합니다. |
| `JwtChannelInterceptor` / `JwtHandshakeInterceptor` | WebSocket Handshake 및 STOMP inbound 메시지의 JWT 인증을 담당합니다. |

### 보안 설정 원칙

- 세션은 `STATELESS`이며 기본 Form Login·HTTP Basic·Logout·CSRF는 비활성화합니다.
- `/api/auth/**`, OAuth2 경로, Swagger, `/ws/**`는 공개 경로입니다.
- 그 외 URL은 `SecurityConfig`의 URL 규칙과 Controller의 `@PreAuthorize`를 함께 적용합니다.
- CORS는 등록된 localhost·운영 프론트 Origin에서 인증 Header와 Cookie를 포함한 요청을 허용합니다.

## 5. 설정·관측·운영 컴포넌트

| 컴포넌트 | 역할 | 주요 정책 |
| --- | --- | --- |
| `TraceIdFilter` | 요청 단위 로그 추적 | 8자리 traceId를 MDC·`X-Trace-Id`에 저장하고 요청 종료 후 MDC를 비웁니다. |
| `Monitored` + `MetricMonitoringAspect` | 메서드 처리 시간 측정 | annotation이 붙은 메서드의 성공·실패·예외 클래스와 percentile 메트릭을 Micrometer에 기록합니다. 메트릭 기록 실패는 본 요청을 실패시키지 않습니다. |
| `PerformanceLogAspect` | 성능 로그 | 지정된 지점의 실행 시간을 로그로 남깁니다. |
| `AsyncConfig` | 비동기 실행기 | 기본 실행기는 core 5 / max 10 / queue 50이며 MDC를 비동기 스레드로 전파합니다. 메일 전용 실행기는 별도로 둡니다. |
| `CacheConfig` | Caffeine Cache | 캘린더, PT 메인, 운동, 온보딩, 사용자 프로필 등 조회 캐시의 TTL·최대 크기를 설정합니다. |
| Cache Evictor | 캐시 무효화 | Calendar·Exercise·Onboarding·User 변경 후 관련 캐시를 제거합니다. |
| `JpaConfig` | JPA Auditing | 생성·수정 시각 자동 기록을 활성화합니다. |
| `TimeConfig` | 시간 의존성 주입 | `Clock` Bean을 제공해 시간 기반 로직과 테스트의 결정성을 높입니다. |
| `OpenAPIConfig` | Swagger/OpenAPI | API 문서 기본 정보를 설정합니다. |
| `WebConfig` | Spring MVC | Web MVC 공통 설정을 제공합니다. |
| `WebSocketConfig` | STOMP WebSocket | `/ws` SockJS Endpoint, `/app` 요청 Prefix, `/topic`·`/queue` Broker, `/user` Destination을 설정합니다. |
| `GlobalRetentionScheduler` | 보존 기간 정리 | 등록된 `RetentionJob`을 설정된 cron·zone에 따라 순차 실행합니다. 한 Job 실패가 다음 Job 실행을 막지 않습니다. |

## 6. 공통 도메인 모델

| 구성 요소 | 역할 |
| --- | --- |
| `DomainEvent` | 도메인 이벤트가 구현할 공통 계약입니다. |
| `AbstractAggregateRootSupport` | Aggregate Root가 도메인 이벤트를 등록·조회·정리할 수 있게 지원합니다. |
| `FileType` | 파일의 논리 타입과 저장 경로 기준을 공유합니다. 실제 MIME·접근 정책은 File 도메인의 `FilePolicy`가 담당합니다. |
| Base Entity 계열 | 생성·수정·삭제 시각이 필요한 JPA Entity가 상속하는 공통 시간 필드 기반 클래스입니다. |

## 7. 개발 시 사용 기준

```text
새 Controller 성공 응답
→ 도메인 ResponseCode enum 정의
→ GlobalApiResponse.ok(...) 또는 created(...) 사용

도메인 정책 실패
→ 해당 도메인 ErrorCode 정의
→ ApplicationException 하위 예외 생성·발생
→ GlobalExceptionHandler가 표준 오류 응답 생성

인증된 사용자 정보 필요
→ @AuthenticationPrincipal AuthUser 사용
→ userId·role을 요청값에서 신뢰하지 않음

시간 측정이 필요한 공통 작업
→ @Monitored 사용 여부 검토
→ 불필요한 고카디널리티 태그는 추가하지 않음
```

> `global`에 특정 도메인의 업무 규칙을 추가하지 않습니다. 공통 기술 정책이나 여러 도메인이 공유하는 계약만 이 패키지에 둡니다. 🧩

## 📝 문서 정보

- 업데이트일: `2026-07-22`
- 변경 사항(요약):
  - 공통 응답·예외·JWT 보안·TraceId 처리의 책임과 호출 흐름을 정리했습니다. 🌐
  - 설정, 캐시, AOP 메트릭, 비동기 실행기, WebSocket, 보존 스케줄러의 운영 역할을 추가했습니다. ⚙️
  - 새 도메인 개발 시 공통 컴포넌트를 사용하는 기준을 기록했습니다. 🧭
