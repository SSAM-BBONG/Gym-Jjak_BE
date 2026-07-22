# 🔔 알림 미읽음 카운트 API 설계

> 작성일: `2026-07-21`  
> 상태: 사용자 설계 승인 완료 · 구현 전 검토 대기

## 1. 목표

프론트엔드가 사용자별 메인 페이지 헤더에 표시할 미읽음 알림 개수를 가볍게 조회할 수 있도록 별도 API를 제공한다.

동시에 실제 이벤트 발행·리스너가 없는 `PT_RESERVATION_REJECTED` 알림 타입을 제거한다.

## 2. 확정 API 계약

```text
GET /api/notifications/unread-count
Authorization: Bearer {accessToken}
```

성공 응답:

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

### 카운트 대상 조건

```text
receiver_id = 로그인 사용자 ID
AND read_at IS NULL
AND deleted_at IS NULL
AND expires_at > 현재 시각
```

- 목록 조회와 동일하게 삭제·만료 알림은 제외한다.
- 로그인 사용자 ID는 Access Token에서만 얻는다. 요청 파라미터·본문으로 사용자 ID를 받지 않는다.
- 별도 역할 제한은 두지 않고 현재 `/api/notifications/**`의 `authenticated()` 정책을 따른다.

## 3. 아키텍처와 데이터 흐름

```text
클라이언트
→ NotificationController.findUnreadNotificationCount(authUser)
→ NotificationQueryUseCase.findUnreadNotificationCount(userId)
→ NotificationQueryService
→ NotificationQueryPort.countUnreadNotifications(userId, now)
→ NotificationRepositoryAdapter
→ SpringDataNotificationRepository COUNT 쿼리
→ UnreadNotificationCountResult
→ UnreadNotificationCountResponse
→ NOTIFICATION_200_5 응답
```

### 계층별 책임

| 계층 | 변경 책임 |
| --- | --- |
| Presentation | 인증 사용자 ID를 전달하고 Web 응답 DTO로 변환한다. |
| Application | 사용자 ID를 검증하고 조회 Port를 호출한다. |
| Port | 미읽음 개수 조회 계약을 정의한다. |
| Persistence | 읽지 않음·미삭제·미만료 조건의 단일 `COUNT` 쿼리를 실행한다. |

## 4. 알림 타입 정리

`NotificationType.PT_RESERVATION_REJECTED`를 제거한다.

근거:

- `PtReservationRejectedEvent`가 존재하지 않는다.
- 해당 이벤트를 발행하는 PT 예약 Service 코드가 없다.
- 해당 이벤트를 수신하는 `PtReservationNotificationEventListener` 메서드가 없다.

따라서 현재 enum 삭제는 실제 알림 생성 흐름을 변경하지 않는다. 삭제 전 전체 참조 검색과 컴파일로 안전성을 검증한다.

## 5. 오류 처리

| 상황 | HTTP 상태 | 코드 |
| --- | --- | --- |
| 유효하지 않은 사용자 ID | 400 | `NOTIFICATION_400_1` |
| 인증 토큰 없음 또는 유효하지 않음 | 401 | `COMMON_401` |
| 예상하지 못한 서버 오류 | 500 | `COMMON_500` |

Controller 요청에는 사용자 ID가 노출되지 않으므로 정상 HTTP 호출에서 400은 발생하기 어렵다. 다만 Application 계층은 기존 Query Service와 동일하게 방어 검증을 유지한다.

## 6. 테스트 계획

1. Query Service: 정상 userId가 Port에서 받은 미읽음 개수를 그대로 반환한다.
2. Query Service: `null` 또는 0 이하 userId는 `InvalidNotificationException`을 던진다.
3. Persistence: 수신자, 미읽음, 미삭제, 미만료 조건만 COUNT 쿼리에 포함된다.
4. Controller: 인증 사용자는 `NOTIFICATION_200_5`와 `data.unreadCount`를 받는다.
5. Enum 정리 후 전체 참조 검색과 컴파일을 실행한다.

## 7. 문서화 범위

- `src/main/java/.../notification/docs/API.md`에 새 API 명세를 추가한다.
- `API_FLOW.md`에 헤더 카운트 조회 흐름을 추가한다.
- 기존 `TROUBLESHOOTING.md`는 이번 기능과 직접 관련 없는 보존 정책 이슈이므로 변경하지 않는다.

## 8. 범위 제외

- 알림 목록 응답에 `unreadCount`를 중복으로 추가하지 않는다.
- 전체 읽음 API는 구현하지 않는다. 현재 `NOTIFICATION_ALL_READ` 응답 코드는 이번 범위에서 변경하지 않는다.
- 만료 알림 보존 정책 문제는 별도 작업으로 유지한다.

## 9. 자체 검토

- [x] API 경로는 기존 `/api/notifications` 하위로 유지한다.
- [x] 목록과 헤더 카운트의 노출 조건이 일치한다.
- [x] 사용자 ID는 인증 정보에서만 얻는다.
- [x] enum 삭제가 현재 이벤트 흐름에 영향을 주지 않는지 참조 검색으로 확인했다.
- [x] 구현 범위와 보존 정책 개선 범위를 분리했다.
