# 📝 피드백 API

> 작성일: 2026-07-21
> 대상: `FeedbackController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 권한: 등록·수정·삭제는 `TRAINER`, 목록·상세 조회는 `USER` 또는 `TRAINER` 권한이 필요합니다.

## 기능 개요

- 📋 트레이너는 완료된 세션에 대해 커리큘럼 단위로 피드백을 작성합니다.
- 🔍 수강생과 트레이너 모두 예약별 피드백 목록과 상세를 조회할 수 있습니다.
- ⚠️ 세션이 완료되지 않은 경우(DB 상태 `COMPLETED`가 아니고 `reservedEndAt`이 현재 시각 이전이 아닌 경우) 피드백을 등록할 수 없습니다.

---

## 1. 피드백 목록 조회

`GET /api/reservations/{reservationId}/feedbacks`

커리큘럼 단위로 피드백 유무를 포함하여 반환합니다.

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
| `200 OK` | `RESERVATION_FEEDBACKS_200` | 피드백 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "RESERVATION_FEEDBACKS_200",
  "message": "피드백 목록 조회 성공",
  "data": [
    {
      "ptCurriculumId": 1,
      "sessionNo": 1,
      "title": "기초 자세 교정",
      "feedbacks": {
        "feedbackId": 5,
        "content": "자세가 많이 좋아졌습니다.",
        "createdAt": "2026-07-10"
      }
    },
    {
      "ptCurriculumId": 2,
      "sessionNo": 2,
      "title": "체력 강화 운동",
      "feedbacks": null
    }
  ]
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data[].ptCurriculumId` | 커리큘럼 ID입니다. |
| `data[].sessionNo` | 회차 번호입니다. |
| `data[].feedbacks` | 해당 회차의 피드백 요약입니다. 피드백이 없으면 `null`입니다. |
| `data[].feedbacks.feedbackId` | 피드백 ID입니다. 상세 조회 시 사용합니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |

---

## 2. 피드백 상세 조회

`GET /api/reservations/{reservationId}/feedbacks/{feedbackId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reservationId` | 예약 ID입니다. |
| `feedbackId` | 조회할 피드백 ID입니다. |

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
| `200 OK` | `FEEDBACK_DETAIL_200` | 피드백 상세 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "FEEDBACK_DETAIL_200",
  "message": "피드백 상세 조회 성공",
  "data": {
    "sessionNo": 1,
    "curriculumTitle": "기초 자세 교정",
    "content": "자세가 많이 좋아졌습니다.",
    "mediaList": [
      {
        "feedbackMediaId": 1,
        "mediaType": "BEFORE",
        "fileUrl": "https://..."
      },
      {
        "feedbackMediaId": 2,
        "mediaType": "AFTER",
        "fileUrl": "https://..."
      }
    ],
    "createdAt": "2026-07-10"
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.mediaList[].mediaType` | 미디어 타입입니다. `BEFORE` 또는 `AFTER` 중 하나입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `FEEDBACK_002` | 접근 권한이 없습니다. | 본인 강습/예약의 피드백이 아닌 경우 |
| `404 Not Found` | `FEEDBACK_001` | 피드백을 찾을 수 없습니다. | 존재하지 않는 피드백인 경우 |

---

## 3. 피드백 등록

`POST /api/reservations/{reservationId}/feedbacks`

세션이 완료된 경우에만 등록할 수 있습니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Path Variable

| name | description |
| --- | --- |
| `reservationId` | 피드백을 등록할 예약 ID입니다. |

Request Body

```json
{
  "ptCurriculumId": 1,
  "content": "자세가 많이 좋아졌습니다.",
  "media": [
    {
      "file": {
        "fileKey": "feedbacks/before.mp4",
        "originalName": "before.mp4",
        "contentType": "video/mp4",
        "fileSize": 10485760
      },
      "mediaType": "BEFORE"
    }
  ]
}
```

| name | 필수 | 설명 |
| --- | --- | --- |
| `ptCurriculumId` | O | 피드백을 등록할 커리큘럼 ID입니다. |
| `content` | O | 피드백 내용입니다. |
| `media` | O | 미디어 목록입니다. 1개 이상, 최대 2개까지 등록할 수 있습니다. |
| `media[].mediaType` | O | `BEFORE` 또는 `AFTER` 중 하나입니다. 각 1개씩만 허용합니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `201 Created` | `FEEDBACK_201` | 피드백 등록 성공 |

Response Body

```json
{
  "status": 201,
  "code": "FEEDBACK_201",
  "message": "피드백 등록 성공",
  "data": { "feedbackId": 5 }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `FEEDBACK_005` | 미디어는 BEFORE/AFTER 각 1개씩만 등록할 수 있습니다. | `BEFORE`/`AFTER` 각 1개 초과 또는 미디어 타입 중복인 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `FEEDBACK_002` | 접근 권한이 없습니다. | 본인 강습의 예약이 아닌 경우 |
| `404 Not Found` | `FEEDBACK_003` | 커리큘럼을 찾을 수 없습니다. | 존재하지 않는 커리큘럼인 경우 |
| `409 Conflict` | `FEEDBACK_004` | 해당 회차에 이미 피드백이 존재합니다. | 해당 커리큘럼에 이미 피드백이 있는 경우 |
| `409 Conflict` | `FEEDBACK_007` | 완료되지 않은 세션에는 피드백을 작성할 수 없습니다. | 세션이 완료되지 않은 경우 |

---

## 4. 피드백 수정

`PATCH /api/reservations/{reservationId}/feedbacks/{feedbackId}`

미디어는 기존 미디어를 전부 교체합니다. 등록과 동일하게 파일 메타데이터를 전달합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Path Variable

| name | description |
| --- | --- |
| `reservationId` | 예약 ID입니다. |
| `feedbackId` | 수정할 피드백 ID입니다. |

Request Body

```json
{
  "content": "수정된 피드백 내용입니다.",
  "media": [
    {
      "file": {
        "fileKey": "feedbacks/before_v2.mp4",
        "originalName": "before_v2.mp4",
        "contentType": "video/mp4",
        "fileSize": 10485760
      },
      "mediaType": "BEFORE"
    }
  ]
}
```

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `FEEDBACK_UPDATED` | 피드백이 수정되었습니다 |

Response Body

```json
{
  "status": 200,
  "code": "FEEDBACK_UPDATED",
  "message": "피드백이 수정되었습니다",
  "data": { "feedbackId": 5 }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `FEEDBACK_005` | 미디어는 BEFORE/AFTER 각 1개씩만 등록할 수 있습니다. | 미디어 타입 중복 또는 개수 초과인 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `FEEDBACK_002` | 접근 권한이 없습니다. | 본인 피드백이 아닌 경우 |
| `404 Not Found` | `FEEDBACK_001` | 피드백을 찾을 수 없습니다. | 존재하지 않는 피드백인 경우 |

---

## 5. 피드백 삭제

`DELETE /api/reservations/{reservationId}/feedbacks/{feedbackId}`

예약의 DB 상태가 `COMPLETED`이면 삭제할 수 없습니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reservationId` | 예약 ID입니다. |
| `feedbackId` | 삭제할 피드백 ID입니다. |

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
| `200 OK` | `FEEDBACK_DELETED` | 피드백이 삭제되었습니다 |

Response Body

```json
{
  "status": 200,
  "code": "FEEDBACK_DELETED",
  "message": "피드백이 삭제되었습니다",
  "data": null
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `FEEDBACK_002` | 접근 권한이 없습니다. | 본인 피드백이 아닌 경우 |
| `404 Not Found` | `FEEDBACK_001` | 피드백을 찾을 수 없습니다. | 존재하지 않는 피드백인 경우 |
| `409 Conflict` | `FEEDBACK_006` | 완료된 예약의 피드백은 삭제할 수 없습니다. | 예약 DB 상태가 `COMPLETED`인 경우 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
