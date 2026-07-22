# 🛠️ Notification Troubleshooting

> 알림 도메인에서 코드로 확인된 문제와 재현·대응 방향을 기록합니다.

---

## 1. 만료된 미삭제 알림이 hard delete 대상에 포함되지 않음

### 증상

- 만료된 알림은 목록 조회에서 보이지 않습니다.
- 하지만 사용자가 삭제하지 않은 만료 알림은 보존 작업이 실행돼도 DB에서 제거되지 않을 수 있습니다.
- 결과적으로 `notifications` 테이블에 조회되지 않는 행이 계속 누적될 수 있습니다. ⚠️

### 구현 근거

| 위치 | 현재 동작 |
| --- | --- |
| `Notification.create()` | `expiresAt`을 생성 시각 기준 7일 후로 설정합니다. |
| `SpringDataNotificationRepository.findNotifications()` | `expiresAt > now` 조건으로 만료 알림을 목록에서 제외합니다. |
| `NotificationRetentionService.hardDeleteExpiredNotifications()` | 7일 전 시각을 `threshold`로 계산해 보존 조회를 호출합니다. |
| `findHardDeleteCandidateIds()` | `deleted_at is not null and deleted_at < threshold` 조건만 사용합니다. `expires_at`은 조건에 포함하지 않습니다. |

즉, 알림의 **만료 정책**과 보존 작업의 **삭제 후보 조건**이 서로 다릅니다.

### 재현 시나리오

1. `deletedAt`이 `null`인 알림을 생성합니다.
2. 시간이 지나 `expiresAt <= now` 상태가 되게 합니다.
3. 목록 조회를 호출하면 해당 알림은 반환되지 않습니다.
4. 보존 작업을 실행해도 `deleted_at is null`이므로 삭제 후보에 포함되지 않습니다.

### 영향

- DB 저장 공간과 인덱스 크기가 불필요하게 증가할 수 있습니다.
- 보존 작업 로그는 성공으로 보이지만, 만료 알림 정리라는 기대와 다르게 후보 수가 0일 수 있습니다.
- 알림량이 많은 사용자 환경에서는 장기적으로 조회·백업 비용에 영향을 줄 수 있습니다.

### 대응 방향

정책을 먼저 확정한 뒤 쿼리와 메서드 이름을 함께 일치시켜야 합니다.

1. **만료 알림을 정리하는 정책**이라면, 후보 조회 조건을 `expires_at < 기준 시각`으로 변경합니다.
   - 만료 즉시 정리: 기준 시각을 `now`로 사용
   - 만료 후 N일 보관: 기준 시각을 `now - N일`로 사용
2. **사용자가 삭제한 알림만 정리하는 정책**이라면, 현재 쿼리를 유지하되 `hardDeleteExpiredNotifications`·`notification_retention` 명칭을 soft delete 보존 정책에 맞게 변경합니다.
3. 선택한 정책에 맞는 Repository 통합 테스트를 추가합니다.

> 현재는 문서화만 수행했으며, 쿼리·보존 정책 코드는 변경하지 않았습니다.

---

## 📝 문서 정보

- 업데이트일: `2026-07-21`
- 변경 사항(요약):
  - 만료 알림과 hard delete 후보 조건의 불일치를 코드 근거와 함께 기록했습니다. ⚠️
