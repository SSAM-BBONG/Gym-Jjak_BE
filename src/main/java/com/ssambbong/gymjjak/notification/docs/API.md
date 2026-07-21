# 🔔 알림 API

> 작성일: `2026-07-21`  
> 대상: `NotificationController`  
> 공통 응답 형식: `status`, `code`, `message`, `data`  
> 권한: 유효한 Access Token이 필요합니다. `SecurityConfig`의 기본 인증 규칙을 적용하므로 USER, TRAINER, ORGANIZATION, ADMIN 모두 자신의 알림을 조회·처리할 수 있습니다. 🔒

## 기능 개요

- 📋 로그인한 사용자의 알림을 최신순으로 Slice 방식으로 조회합니다.
- 🔢 메인 페이지 헤더에 표시할 미읽음 알림 개수를 조회합니다.
- ✅ 하나 또는 여러 알림을 읽음 처리합니다.
- 🗑️ 하나 또는 여러 알림을 soft delete 처리합니다.
- ⚡ 알림 생성은 외부 도메인 이벤트로 내부 처리되며, 공개 생성 API는 제공하지 않습니다.

> 알림 목록은 `deletedAt`이 존재하거나 `expiresAt`이 현재 시각보다 같거나 이른 알림을 제외합니다.

---

## 1. 내 알림 목록 조회

`GET /api/notifications?page=0&size=10`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `page` | X | 페이지 번호입니다. 기본값은 `0`이며 0 이상이어야 합니다. |
| `size` | X | 한 페이지의 알림 수입니다. 기본값은 `10`이며 1~50 사이여야 합니다. |

Request Body

```json

```

| name | 설명 |
| --- | --- |
| - | 본 API는 Request Body를 사용하지 않습니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `NOTIFICATION_200_1` | 알림 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "NOTIFICATION_200_1",
  "message": "알림 목록 조회에 성공했습니다.",
  "data": {
    "content": [
      {
        "notificationId": 101,
        "category": "PT",
        "categoryLabel": "PT",
        "type": "PT_RESERVATION_APPROVED",
        "title": "PT 예약 확정 안내",
        "content": "PT 예약이 승인되었습니다.",
        "targetType": "PT_RESERVATION",
        "targetId": 501,
        "eventAt": "2026-07-21T09:30:00",
        "read": false
      }
    ],
    "page": 0,
    "size": 10,
    "hasNext": false
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.content` | 현재 Slice의 알림 목록입니다. 빈 결과는 `[]`입니다. |
| `data.content[].notificationId` | 알림 ID입니다. 읽음·삭제 API에 사용합니다. |
| `data.content[].category` | 알림 대분류입니다. `PT`, `FEEDBACK`, `ORGANIZATION`, `TRAINER` 중 하나입니다. |
| `data.content[].categoryLabel` | 화면 표시용 카테고리명입니다. |
| `data.content[].type` | 세부 알림 타입입니다. 예: `PT_RESERVATION_APPROVED` |
| `data.content[].title` | 알림 제목입니다. |
| `data.content[].content` | 알림 본문입니다. |
| `data.content[].targetType` | 알림 클릭 시 이동할 대상 타입입니다. |
| `data.content[].targetId` | 알림 클릭 시 사용할 대상 ID입니다. 대상 유형에 따라 `null`일 수 있습니다. |
| `data.content[].eventAt` | 원본 이벤트 발생 시각입니다. 원본 이벤트가 시각을 전달하지 않은 경우 `null`일 수 있습니다. |
| `data.content[].read` | 읽음 여부입니다. |
| `data.page` | 현재 페이지 번호입니다. 0부터 시작합니다. |
| `data.size` | 요청에 적용된 페이지 크기입니다. |
| `data.hasNext` | 다음 Slice 존재 여부입니다. 총 개수와 총 페이지 수는 반환하지 않습니다. |

### 조회 정책

- 정렬은 생성 일시 내림차순, 알림 ID 내림차순입니다.
- soft delete된 알림과 만료된 알림은 조회되지 않습니다.
- 목록에는 로그인한 사용자가 수신자인 알림만 반환됩니다.

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | `page`, `size`가 허용 범위를 벗어난 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

---

## 2. 내 미읽음 알림 개수 조회

`GET /api/notifications/unread-count`

메인 페이지 헤더에 표시할 활성 미읽음 알림 개수를 조회합니다. 🔢

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Request Parameter

없음

Request Body

```json

```

| name | 설명 |
| --- | --- |
| - | 본 API는 Request Body를 사용하지 않습니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `NOTIFICATION_200_5` | 미읽음 알림 개수 조회 성공 |

Response Body

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

### Response Field

| name | 설명 |
| --- | --- |
| `data.unreadCount` | 로그인 사용자의 미읽음·미삭제·미만료 알림 개수입니다. |

### 조회 정책

- 현재 사용자가 수신자인 알림만 집계합니다.
- `readAt`이 `null`인 읽지 않은 알림만 집계합니다.
- soft delete 또는 만료된 알림은 집계에서 제외합니다.

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

---

## 3. 알림 읽음 처리

`PATCH /api/notifications/read`

동일 ID가 여러 번 전달되면 중복을 제거한 뒤 한 번만 처리합니다. 이미 읽은 알림은 오류 없이 성공으로 처리합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Request Parameter

없음

Request Body

```json
{
  "notificationIds": [101, 102]
}
```

| name | 설명 |
| --- | --- |
| `notificationIds` | 읽음 처리할 알림 ID 목록입니다. 비어 있을 수 없고, 각 ID는 양수여야 하며 최대 100개까지 전달할 수 있습니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `NOTIFICATION_200_2` | 알림 읽음 처리 성공 |

Response Body

```json
{
  "status": 200,
  "code": "NOTIFICATION_200_2",
  "message": "알림 읽음 처리가 완료되었습니다.",
  "data": {
    "readNotificationIds": [101, 102]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.readNotificationIds` | 실제 읽음 처리 대상으로 확인된 알림 ID 목록입니다. 중복 요청 ID는 한 번만 포함됩니다. |

### 처리 정책

- 요청 목록 중 하나라도 없거나, 다른 사용자의 알림이거나, 삭제·만료 상태이면 전체 요청은 실패합니다.
- 검증이 모두 끝난 뒤 변경 대상을 `saveAll`로 한 번에 저장하므로 부분 성공 응답은 반환하지 않습니다.

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | ID 목록이 비어 있거나 100개를 초과하거나 0 이하 또는 `null` ID가 포함된 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `NOTIFICATION_403_1` | 알림에 대한 접근 권한이 없습니다. | 다른 사용자의 알림 ID를 요청한 경우 |
| `404 Not Found` | `NOTIFICATION_404_1` | 알림을 찾을 수 없습니다. | 존재하지 않거나 삭제·만료된 알림 ID를 요청한 경우 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

---

## 4. 알림 삭제

`DELETE /api/notifications`

알림을 즉시 hard delete하지 않고 `deletedAt`을 기록하는 soft delete로 처리합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Request Parameter

없음

Request Body

```json
{
  "notificationIds": [101, 102]
}
```

| name | 설명 |
| --- | --- |
| `notificationIds` | 삭제할 알림 ID 목록입니다. 비어 있을 수 없고, 각 ID는 양수여야 하며 최대 100개까지 전달할 수 있습니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `NOTIFICATION_200_4` | 알림 삭제 성공 |

Response Body

```json
{
  "status": 200,
  "code": "NOTIFICATION_200_4",
  "message": "알림 삭제가 완료되었습니다.",
  "data": {
    "deletedNotificationIds": [101, 102]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.deletedNotificationIds` | soft delete 처리된 알림 ID 목록입니다. 중복 요청 ID는 한 번만 포함됩니다. |

### 처리 정책

- 요청 목록 중 하나라도 없거나, 다른 사용자의 알림이거나, 이미 삭제·만료 상태이면 전체 요청은 실패합니다.
- 삭제된 알림은 목록 조회에서 즉시 제외됩니다.

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | ID 목록이 비어 있거나 100개를 초과하거나 0 이하 또는 `null` ID가 포함된 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `NOTIFICATION_403_1` | 알림에 대한 접근 권한이 없습니다. | 다른 사용자의 알림 ID를 요청한 경우 |
| `404 Not Found` | `NOTIFICATION_404_1` | 알림을 찾을 수 없습니다. | 존재하지 않거나 이미 삭제·만료된 알림 ID를 요청한 경우 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

---

## 📝 문서 정보

- 업데이트일: `2026-07-21`
- 변경 사항(요약):
  - 메인 페이지 헤더용 `GET /api/notifications/unread-count` API 명세를 추가했습니다. 🔢
  - `NotificationController`의 목록 조회, 읽음 처리, 삭제 API 명세를 구현 기준으로 작성했습니다. 🔔
