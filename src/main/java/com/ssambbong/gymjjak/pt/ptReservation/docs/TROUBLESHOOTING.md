# PT 예약 도메인 트러블슈팅

> 이 문서는 실제 코드 흐름 기반으로 작성됐습니다. 로그 이벤트 키로 원인을 추적합니다.

---

## 1. PT 예약 생성이 실패한다

`event=pt_reservation_create_failed` 로그의 `reason` 값으로 원인을 구분합니다.

| reason | 에러코드 | 원인 | 조치 |
|---|---|---|---|
| `pt_course_not_found` | — | PT 코스 조회 실패 | ptCourseId 확인 |
| `payment_required` | PT_RESERVATION_008 | 해당 코스 결제 완료 기록 없음 | `payments` 테이블에서 `userId + ptCourseId` 결제 상태 확인 |
| `invalid_time` | PT_RESERVATION_001 | 시작/종료 시간이 null이거나 종료 <= 시작 | 요청 시간 값 확인 |
| `limit_exceeded` | PT_RESERVATION_007 | 소모된 세션 수 >= 총 회차 수 | `countConsumedByUserIdAndPtCourseId()` 결과 확인 |
| `schedule_mismatch` | PT_RESERVATION_006 | 코스 스케줄에 없는 요일·시간 | PT 코스의 등록된 스케줄과 요청 시간 비교 |
| `duplicate` | PT_RESERVATION_003 | 동일 시간대에 이미 예약 존재 | `existsByPtCourseIdAndTimeOverlap()` 조건 확인 |

---

## 2. PT 예약 상태 변경이 안 된다 (트레이너)

`event=pt_reservation_status_change_failed` 로그의 `reason` 값으로 원인을 구분합니다.

| reason | 에러코드 | 원인 | 조치 |
|---|---|---|---|
| `not_found` | PT_RESERVATION_002 | 예약 조회 실패 | ptReservationId 확인 |
| `forbidden` | PT_RESERVATION_004 | 트레이너 프로필 없거나 본인 강습 예약이 아님 | 트레이너 프로필 조회 결과 확인 |

상태 전이 규칙: `RESERVED`는 직접 변경 불가 (PT_RESERVATION_005). `COMPLETED` 요청 시 해당 유저의 전체 세션이 일괄 완료 처리(`bulkCompleteByUserIdAndPtCourseId()`)됩니다.

---

## 3. PT 세션 취소 결과가 예상과 다르다 (당일 취소 → 노쇼 처리)

`cancelPtSession()`은 당일 취소와 전날 이전 취소를 구분합니다.

| 취소 시점 | 처리 결과 |
|---|---|
| `예약 시작일 == 오늘` | `COMPLETED` (노쇼로 간주, 세션 소모) |
| `예약 시작일 > 오늘` | `CANCELLED` (세션 소모 안 됨) |

이미 `CANCELLED` 또는 `COMPLETED` 상태인 세션에 취소를 시도하면 `PT_RESERVATION_005` (409) 에러가 발생합니다.

---

## 4. PT 코스 전체 취소 후 일부 세션이 남아 있다

`cancelPtReservation()`은 해당 유저 + PT 코스의 `COMPLETED`를 제외한 모든 예약을 `CANCELLED`로 일괄 처리합니다(`bulkCancelByUserIdAndPtCourseId()`). 이미 `COMPLETED`된 세션은 취소되지 않습니다. 정상 동작입니다.

---

## 5. 예약 후 캘린더에 반영이 안 된다

예약 생성·취소·상태 변경 시 트랜잭션 커밋 이후(`afterCommit`) `calendarCacheEvictionPort.evictMonth(userId, reservedStartAt)`를 호출합니다.

- 트랜잭션이 정상 커밋됐는데도 캘린더가 갱신되지 않으면 `CalendarCacheEvictionPort` 구현체 동작을 확인합니다.
- 트랜잭션이 롤백된 경우 캐시 무효화는 호출되지 않습니다.

---

## 6. 자동 완료 스케줄러가 동작하지 않는다

`PtReservationAutoCompleteScheduler`가 매시 정각(KST)에 실행됩니다.

```
event=pt_auto_in_progress_succeeded count=N  ← IN_PROGRESS로 전환된 건수
event=pt_auto_complete_succeeded count=N     ← COMPLETED로 자동 완료된 건수
```

로그가 전혀 없으면 스케줄러가 실행되지 않은 것입니다. `@EnableScheduling` 활성화 여부와 배포 환경 확인이 필요합니다.

---

## 7. PT 리마인더 알림이 오지 않는다

`PtReminderScheduler`가 매시 정각(KST)에 실행되며, 정확히 1시간 후 시작하는 예약을 대상으로 알림을 발행합니다.

```
event=pt_reminder_scheduler_started from=, to=
event=pt_reminder_scheduler_completed
```

- 알림이 오지 않으면 대상 예약의 `reservedStartAt`이 쿼리 범위(`from ~ to`)에 포함되는지 확인합니다.
- `NotificationEventProcessor.createSafely()` 실패 시 알림만 누락되고 예약 데이터에는 영향 없습니다.

---

## 8. 주요 로그 이벤트 키 목록

| 이벤트 키 | 의미 |
|---|---|
| `event=pt_reservation_create_failed` | 예약 생성 실패 (reason 참고) |
| `event=pt_reservation_create_succeeded` | 예약 생성 성공 |
| `event=pt_reservation_status_change_failed` | 상태 변경 실패 (reason 참고) |
| `event=pt_reservation_status_change_succeeded` | 상태 변경 성공 |
| `event=pt_reservation_cancel_failed` | 코스 전체 취소 실패 (reason 참고) |
| `event=pt_reservation_cancel_succeeded` | 코스 전체 취소 성공 |
| `event=pt_session_cancel_failed` | 세션 개별 취소 실패 (reason 참고) |
| `event=pt_session_cancel_succeeded` | 세션 개별 취소 성공 |
| `event=pt_auto_in_progress_succeeded` | 자동 IN_PROGRESS 전환 완료 |
| `event=pt_auto_complete_succeeded` | 자동 COMPLETED 전환 완료 |
| `event=pt_reminder_scheduler_started` | 리마인더 스케줄러 시작 |
| `event=pt_reminder_scheduler_completed` | 리마인더 스케줄러 완료 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
