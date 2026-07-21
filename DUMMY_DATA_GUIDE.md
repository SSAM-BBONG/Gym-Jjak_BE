# 🧪 더미 데이터 설정 가이드

개발·Postman 테스트에 필요한 더미 데이터는 **최신 DB 덤프를 먼저 확인한 뒤** 생성합니다.  
스키마, FK, enum 값, 기존 데이터가 마이그레이션마다 달라질 수 있으므로 이전 SQL을 그대로 재사용하지 않습니다.

---

## 1. 운영 원칙

- ✅ 새 마이그레이션 반영 후에는 최신 DB 덤프를 기준으로 더미 SQL을 다시 작성합니다.
- ✅ 더미 데이터는 개발자가 MySQL Workbench 등에서 직접 실행합니다.
- ✅ Codex는 덤프를 확인해 실행용 SQL과 Postman 기대 결과를 채팅으로 제공합니다.
- ❌ Flyway 마이그레이션 파일에 테스트용 더미 데이터를 넣지 않습니다.
- ❌ Codex가 별도 확인 없이 실제 DB·덤프 파일에 자동으로 INSERT 하지 않습니다.

---

## 2. 더미 데이터 요청 방법

새 더미 데이터가 필요하면 최신 덤프 폴더의 **절대 경로**와 아래 정보를 채팅으로 전달합니다.

```text
[더미 데이터 요청]
덤프 경로: C:\\...\\DumpYYYYMMDD
대상 계정: user01@test.com (userId=1)
대상 기능: 알림 미읽음 개수 조회
필요 건수: 미읽음 알림 5건
검증 API: GET /api/notifications/unread-count
```

덤프 경로만 전달해도 스키마를 먼저 확인할 수 있지만, 대상 계정·기능·건수를 함께 주면 더 정확한 SQL을 만들 수 있습니다. 🙌

---

## 3. Codex 확인 절차

```text
최신 DB 덤프 경로 전달
→ 대상 테이블 DDL 확인
→ 관련 사용자·대상 엔티티의 실제 ID 및 기존 데이터 확인
→ NOT NULL / FK / enum / 기본값 확인
→ 중복·기존 미읽음 건수 반영
→ 실행용 INSERT SQL과 검증 SELECT 제공
→ 개발자가 로컬 DB에 직접 실행
→ Postman으로 API 응답 확인
```

### 확인 항목

| 항목 | 확인 이유 |
| --- | --- |
| 테이블 컬럼·타입 | 마이그레이션으로 추가·삭제된 컬럼 누락 방지 |
| NOT NULL·기본값 | INSERT 실패 방지 |
| 외래키 | 존재하지 않는 사용자·대상 ID 참조 방지 |
| enum 값 | 애플리케이션 역직렬화 오류 방지 |
| 기존 데이터 | 기대 건수와 실제 API 응답 차이 방지 |

---

## 4. 알림 더미 데이터 기준

헤더 미읽음 알림 개수 API는 아래 조건을 모두 만족하는 알림만 집계합니다.

```sql
receiver_id = :userId
AND read_at IS NULL
AND deleted_at IS NULL
AND expires_at > NOW(6)
```

따라서 알림 더미 데이터 요청 시 아래 값을 기본으로 설정합니다.

| 컬럼 | 기본 설정 | 설명 |
| --- | --- | --- |
| `receiver_id` | 테스트 대상 사용자 ID | 알림 수신자입니다. |
| `read_at` | `NULL` | 미읽음 상태입니다. |
| `deleted_at` | `NULL` | 삭제되지 않은 상태입니다. |
| `expires_at` | 미래 시각 | 만료되지 않은 상태입니다. |
| `notification_type` | 최신 enum의 유효 값 | 애플리케이션에서 읽을 수 있어야 합니다. |
| `category`, `title`, `content` | 최신 스키마의 필수 값 | NOT NULL 제약을 만족해야 합니다. |
| `target_type`, `target_id` | 실제 대상 또는 `NULL` | 알림 클릭 이동이 필요하면 유효한 대상을 사용합니다. |

> ⚠️ `targetId`는 수신자 사용자 ID가 아닙니다. 알림 클릭 시 이동할 PT 예약·피드백·신청서 등의 대상 ID입니다.

---

## 5. Postman 검증 기준

더미 SQL 실행 전후로 아래 쿼리를 실행해 기대 건수를 먼저 확인합니다.

```sql
SELECT COUNT(*) AS unreadCount
FROM notifications
WHERE receiver_id = :userId
  AND read_at IS NULL
  AND deleted_at IS NULL
  AND expires_at > NOW(6);
```

그 다음 해당 계정의 Access Token으로 아래 API를 호출합니다.

```text
GET /api/notifications/unread-count
Authorization: Bearer {accessToken}
```

`data.unreadCount`는 위 SQL의 `unreadCount`와 같아야 합니다. 🔢

---

## 6. 마이그레이션 이후 갱신 규칙

마이그레이션으로 테이블·제약 조건·enum·대상 도메인 구조가 변경되면 다음 순서로 진행합니다.

```text
마이그레이션 적용
→ 최신 DB 덤프 생성
→ 새 덤프 경로를 Codex 채팅에 전달
→ 더미 데이터 SQL 재검토·재작성
→ 검증 SQL과 Postman 기대값 갱신
```

이 문서는 요청 절차와 검증 기준을 유지합니다. 실제 INSERT SQL은 **덤프 확인 시점의 최신 구조**를 기준으로 채팅에서 제공합니다.

---

## 📝 문서 정보

- 업데이트일: `2026-07-21`
- 변경 사항(요약):
  - 최신 DB 덤프를 기준으로 더미 데이터 SQL을 제공하는 팀 공용 절차를 추가했습니다. 🧪
  - 알림 미읽음 개수 API의 데이터 조건과 Postman 검증 방법을 정리했습니다. 🔢
