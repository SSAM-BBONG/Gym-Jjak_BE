# Notification Unread Count Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 로그인 사용자가 헤더에 표시할 미읽음·미삭제·미만료 알림 개수를 별도 API로 조회하게 한다.

**Architecture:** 기존 Notification Query 축을 확장한다. Controller는 인증 사용자 ID만 전달하고, Query Service가 사용자 ID를 검증한 후 Query Port로 단일 COUNT 조회를 위임한다. Persistence Adapter는 현재 시각과 함께 Spring Data Repository의 미읽음·미삭제·미만료 조건 COUNT 쿼리를 호출한다.

**Tech Stack:** Java 17, Spring Boot 3.5, Spring MVC, Spring Security, Spring Data JPA, JUnit 5, Mockito, MockMvc

## Global Constraints

- API 경로는 `GET /api/notifications/unread-count`로 고정한다.
- 집계 조건은 `receiver_id = userId AND read_at IS NULL AND deleted_at IS NULL AND expires_at > now`로 고정한다.
- 사용자 ID는 `@AuthenticationPrincipal AuthUser`에서만 가져오며 Request Parameter·Body로 받지 않는다.
- `PT_RESERVATION_REJECTED`만 삭제한다. `NOTIFICATION_ALL_READ`는 이번 범위에서 변경하지 않는다.
- 변경된 핵심 코드 바로 위에 한 줄 한국어 주석을 둔다.
- 새 영구 문서는 `src/main/java/com/ssambbong/gymjjak/notification/docs/`에서 업데이트일과 변경 요약을 함께 갱신한다.

---

## File Structure

| 파일 | 변경 목적 |
| --- | --- |
| `notification/application/result/UnreadNotificationCountResult.java` | Application 계층 미읽음 개수 결과를 표현한다. |
| `notification/application/port/out/NotificationQueryPort.java` | 미읽음 개수 조회 Port 계약을 추가한다. |
| `notification/application/usecase/NotificationQueryUseCase.java` | Controller가 호출할 UseCase 계약을 추가한다. |
| `notification/application/service/NotificationQueryService.java` | 사용자 ID 검증과 Port 호출을 수행한다. |
| `notification/infrastructure/persistence/SpringDataNotificationRepository.java` | 단일 COUNT JPQL 쿼리를 추가한다. |
| `notification/infrastructure/persistence/NotificationRepositoryAdapter.java` | Query Port 구현을 추가한다. |
| `notification/presentation/api/response/UnreadNotificationCountResponse.java` | Web 응답 DTO를 추가한다. |
| `notification/presentation/api/response/NotificationResponseCode.java` | `NOTIFICATION_200_5` 성공 코드를 추가한다. |
| `notification/presentation/api/NotificationController.java` | GET 엔드포인트를 추가한다. |
| `notification/domain/type/NotificationType.java` | 미사용 `PT_RESERVATION_REJECTED` enum을 삭제한다. |
| `notification/docs/API.md` | API 명세를 추가한다. |
| `notification/docs/API_FLOW.md` | 헤더 카운트 조회 흐름을 추가한다. |
| `notification/application/service/NotificationQueryServiceTest.java` | Service 단위 테스트를 추가한다. |
| `notification/presentation/api/NotificationControllerTest.java` | Controller Slice 테스트를 추가한다. |

---

### Task 1: Query 계약과 COUNT 조회 구현

**Files:**
- Create: `src/main/java/com/ssambbong/gymjjak/notification/application/result/UnreadNotificationCountResult.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/notification/application/port/out/NotificationQueryPort.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/notification/application/usecase/NotificationQueryUseCase.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/notification/application/service/NotificationQueryService.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/notification/infrastructure/persistence/SpringDataNotificationRepository.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/notification/infrastructure/persistence/NotificationRepositoryAdapter.java`
- Test: `src/test/java/com/ssambbong/gymjjak/notification/application/service/NotificationQueryServiceTest.java`

**Interfaces:**
- Consumes: `NotificationQueryPort`, `NotificationMetric`
- Produces: `UnreadNotificationCountResult(long unreadCount)`, `NotificationQueryUseCase.findUnreadNotificationCount(Long receiverId)`

- [ ] **Step 1: Write the failing Service tests**

```java
@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    @Mock
    private NotificationQueryPort notificationQueryPort;

    @Mock
    private NotificationMetric notificationMetric;

    @InjectMocks
    private NotificationQueryService notificationQueryService;

    @Test
    void findUnreadNotificationCount_returnsCountFromQueryPort() {
        when(notificationQueryPort.countUnreadNotifications(eq(7L), any(LocalDateTime.class)))
                .thenReturn(3L);

        UnreadNotificationCountResult result =
                notificationQueryService.findUnreadNotificationCount(7L);

        assertThat(result.unreadCount()).isEqualTo(3L);
    }

    @Test
    void findUnreadNotificationCount_throwsWhenReceiverIdIsInvalid() {
        assertThatThrownBy(() -> notificationQueryService.findUnreadNotificationCount(0L))
                .isInstanceOf(InvalidNotificationException.class);

        verifyNoInteractions(notificationQueryPort);
    }
}
```

- [ ] **Step 2: Run the Service tests to verify RED**

Run:

```powershell
.\gradlew.bat test --tests "com.ssambbong.gymjjak.notification.application.service.NotificationQueryServiceTest"
```

Expected: `UnreadNotificationCountResult`와 `findUnreadNotificationCount` 또는 `countUnreadNotifications`가 없어 컴파일 실패한다.

- [ ] **Step 3: Add the result, Port, UseCase, Service, and persistence implementation**

```java
// UnreadNotificationCountResult.java
public record UnreadNotificationCountResult(long unreadCount) {
}
```

```java
// NotificationQueryPort.java
long countUnreadNotifications(Long receiverId, LocalDateTime now);
```

```java
// NotificationQueryUseCase.java
UnreadNotificationCountResult findUnreadNotificationCount(Long receiverId);
```

```java
// NotificationQueryService.java
@Override
public UnreadNotificationCountResult findUnreadNotificationCount(Long receiverId) {
    // 로그인 사용자의 활성 미읽음 알림 개수를 단일 조회로 반환합니다.
    validateReceiverId(receiverId);
    long unreadCount = notificationQueryPort.countUnreadNotifications(
            receiverId,
            LocalDateTime.now()
    );
    return new UnreadNotificationCountResult(unreadCount);
}

private void validateReceiverId(Long receiverId) {
    if (receiverId == null || receiverId <= 0) {
        throw new InvalidNotificationException("receiverId는 1 이상이어야 합니다.");
    }
}
```

```java
// SpringDataNotificationRepository.java
@Query("""
        select count(n)
        from NotificationJpaEntity n
        where n.receiverId = :receiverId
          and n.readAt is null
          and n.deletedAt is null
          and n.expiresAt > :now
        """)
long countUnreadNotifications(
        @Param("receiverId") Long receiverId,
        @Param("now") LocalDateTime now
);
```

```java
// NotificationRepositoryAdapter.java
@Override
public long countUnreadNotifications(Long receiverId, LocalDateTime now) {
    // 헤더 표시용 활성 미읽음 알림 수를 단일 COUNT 쿼리로 조회합니다.
    return repository.countUnreadNotifications(receiverId, now);
}
```

- [ ] **Step 4: Run the Service tests to verify GREEN**

Run:

```powershell
.\gradlew.bat test --tests "com.ssambbong.gymjjak.notification.application.service.NotificationQueryServiceTest"
```

Expected: 2 tests pass.

- [ ] **Step 5: Compile production code**

Run:

```powershell
.\gradlew.bat compileJava
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit Task 1**

```powershell
git add -- src/main/java/com/ssambbong/gymjjak/notification/application/result/UnreadNotificationCountResult.java src/main/java/com/ssambbong/gymjjak/notification/application/port/out/NotificationQueryPort.java src/main/java/com/ssambbong/gymjjak/notification/application/usecase/NotificationQueryUseCase.java src/main/java/com/ssambbong/gymjjak/notification/application/service/NotificationQueryService.java src/main/java/com/ssambbong/gymjjak/notification/infrastructure/persistence/SpringDataNotificationRepository.java src/main/java/com/ssambbong/gymjjak/notification/infrastructure/persistence/NotificationRepositoryAdapter.java src/test/java/com/ssambbong/gymjjak/notification/application/service/NotificationQueryServiceTest.java
git commit -m "feat: add unread notification count query"
```

---

### Task 2: 헤더 카운트 API와 enum 정리

**Files:**
- Create: `src/main/java/com/ssambbong/gymjjak/notification/presentation/api/response/UnreadNotificationCountResponse.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/notification/presentation/api/response/NotificationResponseCode.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/notification/presentation/api/NotificationController.java`
- Modify: `src/main/java/com/ssambbong/gymjjak/notification/domain/type/NotificationType.java`
- Test: `src/test/java/com/ssambbong/gymjjak/notification/presentation/api/NotificationControllerTest.java`

**Interfaces:**
- Consumes: `NotificationQueryUseCase.findUnreadNotificationCount(Long receiverId)`
- Produces: `GET /api/notifications/unread-count`, `UnreadNotificationCountResponse(long unreadCount)`

- [ ] **Step 1: Write the failing Controller test**

```java
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationQueryUseCase queryUseCase;

    @MockitoBean
    private NotificationUserCommandUseCase userCommandUseCase;

    @MockitoBean
    private AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @MockitoBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Test
    void findUnreadNotificationCount_returnsCountForAuthenticatedUser() throws Exception {
        AuthUser user = new AuthUser(7L, "user@test.com", "USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
        when(queryUseCase.findUnreadNotificationCount(7L))
                .thenReturn(new UnreadNotificationCountResult(3L));

        mockMvc.perform(get("/api/notifications/unread-count")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("NOTIFICATION_200_5"))
                .andExpect(jsonPath("$.data.unreadCount").value(3));

        verify(queryUseCase).findUnreadNotificationCount(7L);
    }
}
```

- [ ] **Step 2: Run the Controller test to verify RED**

Run:

```powershell
.\gradlew.bat test --tests "com.ssambbong.gymjjak.notification.presentation.api.NotificationControllerTest"
```

Expected: `/api/notifications/unread-count` 매핑 또는 응답 DTO·성공 코드가 없어 실패한다.

- [ ] **Step 3: Add the response code, DTO, endpoint, and enum deletion**

```java
// NotificationResponseCode.java
NOTIFICATION_UNREAD_COUNT_FOUND(
        "NOTIFICATION_200_5",
        "미읽음 알림 개수 조회에 성공했습니다."
);
```

```java
// UnreadNotificationCountResponse.java
@Builder
public record UnreadNotificationCountResponse(long unreadCount) {

    public static UnreadNotificationCountResponse from(UnreadNotificationCountResult result) {
        return UnreadNotificationCountResponse.builder()
                .unreadCount(result.unreadCount())
                .build();
    }
}
```

```java
// NotificationController.java
@Operation(summary = "내 미읽음 알림 개수 조회", description = "헤더 표시용 활성 미읽음 알림 개수를 조회합니다.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "미읽음 알림 개수 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
})
@GetMapping("/unread-count")
public ResponseEntity<GlobalApiResponse<UnreadNotificationCountResponse>> findUnreadNotificationCount(
        @AuthenticationPrincipal AuthUser authUser
) {
    // 헤더 표시용 활성 미읽음 알림 개수를 조회합니다.
    UnreadNotificationCountResult result =
            queryUseCase.findUnreadNotificationCount(authUser.userId());

    return ResponseEntity.ok(GlobalApiResponse.ok(
            NotificationResponseCode.NOTIFICATION_UNREAD_COUNT_FOUND,
            UnreadNotificationCountResponse.from(result)
    ));
}
```

```java
// NotificationType.java
// PT_RESERVATION_REJECTED 상수 블록 전체를 삭제합니다.
```

- [ ] **Step 4: Verify enum deletion has no remaining reference**

Run:

```powershell
rg -n "PT_RESERVATION_REJECTED" src/main/java src/test/java
```

Expected: 결과 없음.

- [ ] **Step 5: Run the Controller test to verify GREEN**

Run:

```powershell
.\gradlew.bat test --tests "com.ssambbong.gymjjak.notification.presentation.api.NotificationControllerTest"
```

Expected: 1 test passes.

- [ ] **Step 6: Commit Task 2**

```powershell
git add -- src/main/java/com/ssambbong/gymjjak/notification/presentation/api/response/UnreadNotificationCountResponse.java src/main/java/com/ssambbong/gymjjak/notification/presentation/api/response/NotificationResponseCode.java src/main/java/com/ssambbong/gymjjak/notification/presentation/api/NotificationController.java src/main/java/com/ssambbong/gymjjak/notification/domain/type/NotificationType.java src/test/java/com/ssambbong/gymjjak/notification/presentation/api/NotificationControllerTest.java
git commit -m "feat: expose unread notification count"
```

---

### Task 3: API 및 흐름 문서 최신화

**Files:**
- Modify: `src/main/java/com/ssambbong/gymjjak/notification/docs/API.md`
- Modify: `src/main/java/com/ssambbong/gymjjak/notification/docs/API_FLOW.md`

**Interfaces:**
- Consumes: `GET /api/notifications/unread-count`, `NOTIFICATION_200_5`, `UnreadNotificationCountResponse`
- Produces: 프론트와 타 도메인 개발자가 참고할 최신 API·흐름 문서

- [ ] **Step 1: Add the API specification**

`API.md`에 다음 계약을 추가한다.

```json
{
  "status": 200,
  "code": "NOTIFICATION_200_5",
  "message": "미읽음 알림 개수 조회에 성공했습니다.",
  "data": {
    "unreadCount": 3
  }
}
```

명세에 반드시 포함할 내용:

- `Authorization: Bearer {accessToken}` 헤더
- Request Parameter와 Request Body 없음
- 삭제·만료·읽음 알림을 제외하는 카운트 조건
- `COMMON_401`, `COMMON_500` 실패 코드
- 문서 하단 업데이트일 `2026-07-21`과 변경 요약

- [ ] **Step 2: Add the header count flow**

`API_FLOW.md`의 조회 API Flow에 아래 코드 블록을 추가한다.

```text
로그인 사용자
→ NotificationController가 Access Token에서 userId 추출
→ NotificationQueryService.findUnreadNotificationCount(userId)
→ NotificationQueryPort.countUnreadNotifications(userId, now)
→ SpringDataNotificationRepository COUNT 조회
→ UnreadNotificationCountResponse
→ NOTIFICATION_200_5 응답
```

`PT_RESERVATION_REJECTED`가 제거되어 PT 예약 반려 알림은 현재 지원하지 않는다는 상태 표기도 삭제한다.

- [ ] **Step 3: Verify documentation and working-tree whitespace**

Run:

```powershell
git diff --check
```

Expected: 출력 없음.

- [ ] **Step 4: Commit Task 3**

```powershell
git add -- src/main/java/com/ssambbong/gymjjak/notification/docs/API.md src/main/java/com/ssambbong/gymjjak/notification/docs/API_FLOW.md
git commit -m "docs: document unread notification count API"
```

---

## Plan Self-Review

- [x] 별도 GET API, COUNT 조건, enum 삭제가 모두 Task 1~2에 포함됐다.
- [x] 목록 API 응답 확장과 전체 읽음 API 추가는 범위에서 제외했다.
- [x] 각 Task에 RED·GREEN 테스트와 검증 명령을 포함했다.
- [x] Task 3가 API 명세와 흐름 문서의 업데이트일·요약을 포함한다.
- [x] 모든 이후 Task의 타입·메서드 이름이 Task 1에서 정의한 계약과 일치한다.
