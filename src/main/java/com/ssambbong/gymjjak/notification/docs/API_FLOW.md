# 🔔 Notification API Flow

> 이 문서는 알림 API와 내부 알림 생성·실시간 전송 흐름을 타 도메인 개발자가 이해할 수 있도록 정리합니다.

---

## 1. 알림 도메인이 담당하는 역할

알림 도메인은 다른 도메인에서 발생한 이벤트를 수신해 사용자별 알림을 저장하고, 연결된 사용자에게 WebSocket으로 실시간 전송합니다.

- 📥 외부 도메인 이벤트를 알림 생성 명령으로 변환합니다.
- 💾 알림을 7일 만료 정책과 함께 저장합니다.
- ⚡ DB 커밋 후 사용자 전용 WebSocket 큐로 알림을 전송합니다.
- 📋 사용자는 자신의 알림만 목록 조회·미읽음 개수 조회·읽음·삭제할 수 있습니다.

```text
PT 예약 / 트레이너 신청 / 피드백 도메인 이벤트
→ Notification Event Listener
→ NotificationEventProcessor
→ NotificationCommandService
→ NotificationRepository에 알림 저장
→ NotificationCreatedEvent 발행
→ 커밋 후 NotificationEventListener
→ WebSocketNotificationSender
→ 사용자 전용 /user/queue/notifications 큐 전송
```

---

## 2. 공통 접근 및 데이터 규칙

### 🔐 접근 제어

- `/api/notifications/**`에는 별도 역할 제한이 없으며, `SecurityConfig`의 최종 `authenticated()` 규칙이 적용됩니다.
- 따라서 유효한 Access Token이 필요하고, 실제 데이터 범위는 Access Token에서 꺼낸 `AuthUser.userId`로 고정됩니다.
- 읽음·삭제는 각 알림의 `receiverId`와 요청자 ID를 다시 비교해 타인의 알림 접근을 차단합니다.

### 🧭 노출 상태

| 상태 | 목록 조회 | 읽음·삭제 요청 |
| --- | --- | --- |
| 정상 | 조회 가능 | 처리 가능 |
| 읽음 | 조회 가능 | 읽음 재요청도 성공 |
| soft delete | 조회 제외 | `NOTIFICATION_404_1` |
| 만료 | 조회 제외 | `NOTIFICATION_404_1` |

---

## 3. API 한눈에 보기

| API | 목적 | 주요 결과 |
| --- | --- | --- |
| `GET /api/notifications` | 내 알림 목록 조회 | 최신순 Slice 목록과 `hasNext` 반환 |
| `GET /api/notifications/unread-count` | 내 미읽음 알림 개수 조회 | 헤더 표시용 활성 미읽음 알림 수 반환 |
| `PATCH /api/notifications/read` | 알림 읽음 처리 | 하나 또는 여러 알림을 읽음 상태로 변경 |
| `DELETE /api/notifications` | 알림 삭제 | 하나 또는 여러 알림을 soft delete 처리 |

---

## 4. 조회 API Flow

### 4.1 내 알림 목록 조회

`GET /api/notifications?page={page}&size={size}`

```text
로그인 사용자
→ NotificationController가 Access Token에서 userId 추출
→ NotificationQueryService.findNotifications(userId, page, size)
→ page·size 검증
→ NotificationQueryPort.findNotifications(query)
→ SpringDataNotificationRepository가 수신자·미삭제·미만료 조건으로 Slice 조회
→ NotificationListResult
→ NotificationListResponse
→ NOTIFICATION_200_1 응답
```

핵심 규칙:

1. `page` 기본값은 0, `size` 기본값은 10입니다.
2. `size`는 최대 50이며, 총 개수와 총 페이지 수 대신 `hasNext`만 반환합니다.
3. 쿼리는 `deletedAt is null`, `expiresAt > now` 조건과 생성 일시 내림차순을 사용합니다.

### 4.2 내 미읽음 알림 개수 조회

`GET /api/notifications/unread-count`

```text
로그인 사용자
→ NotificationController가 Access Token에서 userId 추출
→ NotificationQueryService.findUnreadNotificationCount(userId)
→ receiverId·readAt null·deletedAt null·expiresAt > now 조건으로 COUNT 조회
→ UnreadNotificationCountResponse
→ NOTIFICATION_200_5 응답
```

헤더 카운트는 목록과 동일한 노출 조건을 사용하므로, 삭제·만료·읽음 처리된 알림은 집계하지 않습니다.

---

## 5. 사용자 처리 API Flow

### 5.1 읽음 처리

`PATCH /api/notifications/read`

```text
로그인 사용자
→ NotificationController.readNotifications(userId, notificationIds)
→ NotificationUserCommandService가 입력값 검증·중복 ID 제거
→ NotificationRepository.findAllById(notificationIds)
→ 각 알림의 존재·소유자·미삭제·미만료 상태 검증
→ 읽지 않은 알림만 readAt 설정
→ NotificationRepository.saveAll(changedNotifications)
→ NOTIFICATION_200_2와 처리 ID 목록 응답
```

- 이미 읽은 알림은 변경 없이 성공 ID 목록에 포함됩니다.
- 요청 중 한 알림이라도 타인 소유, 삭제·만료, 미존재이면 예외가 발생하며 트랜잭션은 전체 롤백됩니다.

### 5.2 삭제 처리

`DELETE /api/notifications`

```text
로그인 사용자
→ NotificationController.deleteNotifications(userId, notificationIds)
→ NotificationUserCommandService가 입력값 검증·중복 ID 제거
→ NotificationRepository.findAllById(notificationIds)
→ 각 알림의 존재·소유자·미삭제·미만료 상태 검증
→ 각 알림에 deletedAt 설정
→ NotificationRepository.saveAll(deletedNotifications)
→ NOTIFICATION_200_4와 처리 ID 목록 응답
```

---

## 6. 내부 생성 및 실시간 전송 Flow

알림 생성은 외부 공개 API가 아니라 도메인 이벤트 리스너에서 시작합니다.

```text
원본 도메인 트랜잭션 커밋
→ Notification Event Listener
→ NotificationEventProcessor.createSafely(receiverId, type, targetId, occurredAt)
→ NotificationCommandService.createNotification(command)의 독립 트랜잭션 시작
→ NotificationRepository에 알림 저장
→ NotificationCreatedEvent 발행
→ 알림 생성 트랜잭션 커밋
→ NotificationEventListener가 AFTER_COMMIT에 실행
→ WebSocketNotificationSender가 사용자 전용 /user/queue/notifications 큐로 전송
```

### 6.1 기획 대상별 연결 상태

아래 표는 **코드에서 발행 지점과 알림 리스너까지 확인된 상태**입니다. 저장·실시간 전송은 모든 연결된 이벤트가 공통 흐름을 따릅니다.

| 대상 도메인 | 이벤트 발행 지점 | 수신 이벤트 | 생성 알림 타입 | 코드 연결 상태 |
| --- | --- | --- | --- | --- |
| PT 예약 | 예약 생성 | `PtReservationApprovedEvent` | `PT_RESERVATION_APPROVED` | ✅ 예약 회원에게 생성·전송 |
| PT 예약 | 예약 생성 | `PtReservationRequestedEvent` | `PT_RESERVATION_REQUESTED` | ✅ 트레이너 사용자 ID를 찾은 경우 생성·전송 |
| PT 예약 | 예약 취소 | `PtReservationCanceledEvent` | `PT_RESERVATION_CANCELED` | ✅ 예약 회원에게 생성·전송 |
| 트레이너 신청 | 신청 승인 | `TrainerApplicationApprovedEvent` | `TRAINER_APPLICATION_APPROVED` | ✅ 신청자에게 생성·전송 |
| 트레이너 신청 | 신청 반려 | `TrainerApplicationRejectedEvent` | `TRAINER_APPLICATION_REJECTED` | ✅ 신청자에게 생성·전송 |
| 피드백 | 피드백 등록 | `FeedbackCreatedEvent` | `FEEDBACK_CREATED` | ✅ 예약 회원에게 생성·전송 |

연결된 이벤트의 공통 세부 흐름:

```text
원본 도메인 Service가 이벤트 발행
→ 원본 트랜잭션 AFTER_COMMIT의 알림 리스너가 이벤트 수신
→ NotificationEventProcessor.createSafely(...)
→ NotificationCommandService가 REQUIRES_NEW 트랜잭션으로 알림 저장
→ NotificationCreatedEvent 발행
→ 알림 저장 트랜잭션 AFTER_COMMIT
→ WebSocketNotificationSender가 /user/queue/notifications로 실시간 전송
```

> ⚠️ `createSafely`와 WebSocket 전송 리스너는 런타임 예외를 로그로 남기고 원본 도메인 트랜잭션에는 전파하지 않습니다. 따라서 위 표의 ✅는 **코드 연결이 구현됨**을 뜻하며, 실제 접속 사용자에게의 수신 성공은 WebSocket 통합 테스트 또는 운영 로그·메트릭으로 별도 확인해야 합니다.

핵심 규칙:

- `NotificationCommandService`는 독립 트랜잭션으로 알림을 저장합니다.
- WebSocket 전송은 `AFTER_COMMIT` 이후에 실행되어, 저장에 실패한 알림이 실시간으로 먼저 전송되지 않도록 합니다.
- 실시간 전송 실패는 로그·메트릭으로 기록되지만, 저장된 알림은 목록 API를 통해 다시 조회할 수 있습니다.

---

## 7. 타 도메인 개발자 체크포인트

1. 수신자 ID는 반드시 실제 사용자 ID를 전달합니다.
2. `NotificationType`에 정의된 `targetType`과 대상 ID가 프론트 이동 경로와 일치해야 합니다.
3. 이벤트 발생 시각은 `Instant`로 전달하며, 알림 도메인은 `Asia/Seoul` 기준 `LocalDateTime`으로 변환합니다.
4. 알림 생성 실패는 원본 도메인 흐름을 중단시키지 않고 로그로 남습니다. 중요한 이벤트라면 실패 로그와 저장 메트릭을 모니터링해야 합니다.

---

## 📝 문서 정보

- 업데이트일: `2026-07-21`
- 변경 사항(요약):
  - 헤더 표시용 미읽음 알림 COUNT 조회 흐름을 추가하고, 미사용 PT 예약 반려 알림 타입 설명을 제거했습니다. 🔢
  - 예약·트레이너 신청·피드백 이벤트별 알림 생성·실시간 전송 연결 상태를 추가했습니다. 🔍
  - Mermaid 다이어그램을 Notion에서 바로 읽을 수 있는 일반 화살표 흐름으로 변경했습니다. ➡️
  - 알림 조회·읽음·삭제와 내부 생성·WebSocket 전송 흐름을 정리했습니다. 🔔
