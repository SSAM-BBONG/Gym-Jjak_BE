# 📅 PT 예약 API

> 작성일: 2026-07-21
> 대상: `PtReservationController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 권한: 모든 API는 `USER` 또는 `TRAINER` 권한이 필요합니다. 상태 변경 API는 `TRAINER`만 가능합니다.

## 기능 개요

- 📅 사용자는 결제가 완료된 PT 강습에 대해 예약을 생성할 수 있습니다.
- 📋 사용자는 본인의 예약 목록·상세와 세션 단위 목록을 조회할 수 있습니다.
- ✅ 트레이너는 예약 상태를 변경(완료 등)할 수 있습니다.
- ❌ 수강생과 트레이너 모두 예약을 취소할 수 있습니다.

> ✅ PT 예약 상세 조회 응답의 `ptCourseId`는 강사평 작성 API 호출 시 path variable로 사용합니다.
> ✅ 세션 취소·예약 취소 응답의 `sessionStatus`는 파생 상태가 아닌 취소 후 DB 상태(`CANCELLED`)를 반환합니다.

---

## 1. PT 예약 생성

`POST /api/pt-courses/{ptCourseId}/reservations`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | 예약할 PT 강습 ID입니다. |

Request Body

```json
{
  "reservedStartAt": "2026-08-01T10:00:00",
  "reservedEndAt": "2026-08-01T11:00:00"
}
```

| name | 필수 | 설명 |
| --- | --- | --- |
| `reservedStartAt` | O | 예약 시작 시간입니다. |
| `reservedEndAt` | O | 예약 종료 시간입니다. 시작 시간보다 이후여야 합니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `201 Created` | `PT_RESERVATION_201` | PT 예약 성공 |

Response Body

```json
{
  "status": 201,
  "code": "PT_RESERVATION_201",
  "message": "PT 예약 성공",
  "data": {
    "ptReservationId": 1,
    "status": "RESERVED"
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.status` | 생성된 예약 상태입니다. 완료 세션이 1개 이상이면 `IN_PROGRESS`, 아니면 `RESERVED`입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `PT_RESERVATION_001` | 예약 정보가 유효하지 않습니다. | 시간 null, 종료 시간이 시작 시간 이전인 경우 |
| `400 Bad Request` | `PT_RESERVATION_006` | 강습 스케줄에 없는 요일 또는 시간입니다. | 강습 스케줄에 없는 요일/시간으로 예약 시도한 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `PT_RESERVATION_008` | 결제가 완료된 PT 강습만 예약할 수 있습니다. | 결제가 완료되지 않은 PT 강습 예약 시도인 경우 |
| `404 Not Found` | `PT_COURSE_002` | PT 강습을 찾을 수 없습니다. | 존재하지 않는 PT 강습인 경우 |
| `409 Conflict` | `PT_RESERVATION_003` | 이미 예약된 시간입니다. | 동일 시간대 중복 예약인 경우 |
| `409 Conflict` | `PT_RESERVATION_007` | 예약 가능한 회차 수를 초과하였습니다. | 구매 회차 수 이상 예약 시도인 경우 |

---

## 2. 내 PT 예약 목록 조회

`GET /api/reservations/me?status=RESERVED`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `status` | X | 예약 상태 필터입니다. `RESERVED` / `IN_PROGRESS` / `COMPLETED` / `CANCELLED` 중 하나입니다. 미입력 시 전체를 반환합니다. |

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
| `200 OK` | `MY_PT_RECORDS_200` | 내 PT 기록 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "MY_PT_RECORDS_200",
  "message": "내 PT 기록 목록 조회 성공",
  "data": {
    "ptReservations": [
      {
        "ptReservationId": 1,
        "thumbnailUrl": "https://...",
        "title": "가슴 집중 PT",
        "trainerName": "홍길동",
        "status": "IN_PROGRESS",
        "lastPtDate": "2026-07-10",
        "progressCount": 3,
        "totalSessionCount": 12
      }
    ]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.ptReservations[].status` | 예약 상태입니다. `RESERVED` / `IN_PROGRESS` / `COMPLETED` / `CANCELLED` 중 하나입니다. |
| `data.ptReservations[].lastPtDate` | 마지막 PT 날짜입니다. |
| `data.ptReservations[].progressCount` | 완료된 세션 수입니다. |
| `data.ptReservations[].totalSessionCount` | 전체 회차 수입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |

---

## 3. 내 PT 예약 상세 조회

`GET /api/reservations/me/{reservationId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reservationId` | 조회할 예약 ID입니다. |

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
| `200 OK` | `MY_PT_RECORD_DETAIL_200` | 내 PT 기록 상세 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "MY_PT_RECORD_DETAIL_200",
  "message": "내 PT 기록 상세 조회 성공",
  "data": {
    "ptCourseId": 1,
    "thumbnailUrl": "https://...",
    "title": "가슴 집중 PT",
    "trainerName": "홍길동",
    "status": "IN_PROGRESS",
    "progressCount": 3,
    "totalSessionCount": 12,
    "curriculums": [
      {
        "id": 1,
        "sessionNo": 1,
        "title": "기초 자세 교정",
        "feedbackId": 5
      }
    ]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.ptCourseId` | PT 강습 ID입니다. 강사평 작성 API(`POST /api/pt-courses/{ptCourseId}/reservations/{ptReservationId}/reviews`) 호출 시 사용합니다. |
| `data.status` | 예약 상태입니다. `RESERVED` / `IN_PROGRESS` / `COMPLETED` / `CANCELLED` 중 하나입니다. |
| `data.progressCount` | 완료된 세션 수입니다. |
| `data.curriculums[].feedbackId` | 해당 회차의 피드백 ID입니다. 피드백이 없으면 `null`입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `PT_RESERVATION_004` | 본인의 예약만 조회할 수 있습니다. | 본인 예약이 아닌 경우 |
| `404 Not Found` | `PT_RESERVATION_002` | 예약을 찾을 수 없습니다. | 존재하지 않는 예약인 경우 |

---

## 4. PT 예약 상태 변경 (트레이너)

`PATCH /api/reservations/{reservationId}/status`

`RESERVED`는 예약 생성 시에만 자동 설정되므로 직접 변경할 수 없습니다. `COMPLETED` 요청 시 해당 수강생의 동일 PT 코스 전 세션을 일괄 완료 처리합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Path Variable

| name | description |
| --- | --- |
| `reservationId` | 상태를 변경할 예약 ID입니다. |

Request Body

```json
{ "status": "COMPLETED" }
```

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `PT_RESERVATION_STATUS_200` | PT 수강 상태 변경 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_RESERVATION_STATUS_200",
  "message": "PT 수강 상태 변경 성공",
  "data": {
    "status": "COMPLETED",
    "progressCount": 12,
    "totalSessionCount": 12
  }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `PT_RESERVATION_004` | 본인의 예약만 조회할 수 있습니다. | 본인 강습의 예약이 아닌 경우 |
| `404 Not Found` | `PT_RESERVATION_002` | 예약을 찾을 수 없습니다. | 존재하지 않는 예약인 경우 |
| `409 Conflict` | `PT_RESERVATION_005` | 변경할 수 없는 상태값입니다. | `RESERVED` 직접 설정 시도인 경우 |

---

## 5. PT 예약 취소 (수강생)

`PATCH /api/reservations/me/{reservationId}/cancel`

해당 PT 코스의 `COMPLETED` 상태를 제외한 전 세션을 일괄 취소합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reservationId` | 취소할 예약 ID입니다. |

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
| `200 OK` | `PT_RESERVATION_CANCELLED` | PT 예약이 취소되었습니다 |

Response Body

```json
{
  "status": 200,
  "code": "PT_RESERVATION_CANCELLED",
  "message": "PT 예약이 취소되었습니다",
  "data": {
    "sessionStatus": "CANCELLED",
    "cancelledAt": "2026-07-21T14:30:00"
  }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `PT_RESERVATION_004` | 본인의 예약만 조회할 수 있습니다. | 본인 예약이 아닌 경우 |
| `404 Not Found` | `PT_RESERVATION_002` | 예약을 찾을 수 없습니다. | 존재하지 않는 예약인 경우 |

---

## 6. 내 PT 세션 목록 조회

`GET /api/reservations/me/sessions`

예약 단건(세션) 기준으로 목록을 반환합니다. `sessionStatus`는 DB 상태가 `COMPLETED`이거나 `reservedEndAt`이 현재 시각 이전이면 `COMPLETED`로 반환합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

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
| `200 OK` | `PT_SESSIONS_200` | PT 세션 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_SESSIONS_200",
  "message": "PT 세션 목록 조회 성공",
  "data": {
    "sessions": [
      {
        "ptReservationId": 1,
        "ptCourseId": 1,
        "ptCourseTitle": "가슴 집중 PT",
        "trainerName": "홍길동",
        "reservedStartAt": "2026-08-01T10:00:00",
        "reservedEndAt": "2026-08-01T11:00:00",
        "sessionStatus": "RESERVED"
      }
    ]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.sessions[].sessionStatus` | 세션 상태입니다. DB 상태 `COMPLETED`이거나 `reservedEndAt < now`이면 `COMPLETED`, 그 외는 DB 상태 그대로 반환합니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |

---

## 7. PT 세션 개별 취소

`PATCH /api/reservations/me/sessions/{reservationId}/cancel`

세션 단건을 취소합니다. `COMPLETED` 또는 `CANCELLED` 상태이면 취소할 수 없습니다. 당일 취소는 노쇼로 간주하여 `COMPLETED` 처리됩니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reservationId` | 취소할 세션(예약) ID입니다. |

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
| `200 OK` | `PT_SESSION_CANCELLED` | PT 세션이 취소되었습니다 |

Response Body

```json
{
  "status": 200,
  "code": "PT_SESSION_CANCELLED",
  "message": "PT 세션이 취소되었습니다",
  "data": {
    "sessionStatus": "CANCELLED | COMPLETED",
    "cancelledAt": "2026-07-21T14:30:00"
  }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `PT_RESERVATION_004` | 본인의 예약만 조회할 수 있습니다. | 본인 예약이 아닌 경우 |
| `404 Not Found` | `PT_RESERVATION_002` | 예약을 찾을 수 없습니다. | 존재하지 않는 예약인 경우 |
| `409 Conflict` | `PT_RESERVATION_005` | 변경할 수 없는 상태값입니다. | 이미 `COMPLETED` 또는 `CANCELLED` 상태인 경우 |

> **노쇼 처리**: `reservedStartAt` 날짜가 오늘인 경우 취소 요청 시 `CANCELLED`가 아닌 `COMPLETED`로 처리됩니다. 응답의 `sessionStatus` 필드를 기준으로 처리 결과를 확인합니다.

---

## 📝 문서 정보

- 작성일: `2026-07-21`
