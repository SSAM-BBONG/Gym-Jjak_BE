# ⭐ 강사평 API

> 작성일: 2026-07-21
> 대상: `TrainerReviewController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 권한: 작성·수정·삭제는 `USER` 권한이 필요합니다. 요약·목록 조회는 인증이 불필요합니다.

## 기능 개요

- ⭐ 수강생은 완료된 PT 예약에 대해 강사평을 작성할 수 있습니다.
- 📋 누구나 트레이너 프로필별 강사평 요약(평균 별점, 분포)과 목록을 조회할 수 있습니다.
- 강사평 작성 시 path variable인 `ptCourseId`는 PT 예약 상세 조회(`GET /api/reservations/me/{reservationId}`) 응답의 `ptCourseId`를 사용합니다.

---

## 1. 강사평 작성

`POST /api/pt-courses/{ptCourseId}/reservations/{ptReservationId}/reviews`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | PT 강습 ID입니다. PT 예약 상세 조회(`GET /api/reservations/me/{reservationId}`) 응답의 `ptCourseId`를 사용합니다. |
| `ptReservationId` | PT 예약 ID입니다. |

Request Body

```json
{
  "rating": 5,
  "content": "정말 좋은 트레이너입니다. 체계적인 수업 감사합니다."
}
```

| name | 필수 | 설명 |
| --- | --- | --- |
| `rating` | O | 별점입니다. 1~5 사이의 정수입니다. |
| `content` | O | 강사평 내용입니다. 최대 500자입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `201 Created` | `REVIEW_201` | 강사평이 등록되었습니다. |

Response Body

```json
{
  "status": 201,
  "code": "REVIEW_201",
  "message": "강사평이 등록되었습니다.",
  "data": { "reviewId": 1 }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `REVIEW_004` | 완료된 PT 예약에만 강사평을 작성할 수 있습니다. | 완료되지 않은 예약에 강사평 작성 시도인 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `REVIEW_003` | 접근 권한이 없습니다. | 본인 예약이 아닌 경우 |
| `404 Not Found` | `REVIEW_005` | PT 예약을 찾을 수 없습니다. | 존재하지 않는 예약인 경우 |
| `409 Conflict` | `REVIEW_002` | 해당 예약에 이미 강사평이 존재합니다. | 동일 예약에 강사평을 중복 작성 시도인 경우 |

---

## 2. 강사평 수정

`PATCH /api/reviews/{reviewId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Path Variable

| name | description |
| --- | --- |
| `reviewId` | 수정할 강사평 ID입니다. |

Request Body

```json
{
  "rating": 4,
  "content": "수정된 강사평 내용입니다."
}
```

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `REVIEW_UPDATED` | 강사평 수정 성공 |

Response Body

```json
{
  "status": 200,
  "code": "REVIEW_UPDATED",
  "message": "강사평 수정 성공",
  "data": { "reviewId": 1 }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `REVIEW_003` | 접근 권한이 없습니다. | 본인 강사평이 아닌 경우 |
| `404 Not Found` | `REVIEW_001` | 강사평을 찾을 수 없습니다. | 존재하지 않는 강사평인 경우 |

---

## 3. 강사평 삭제

`DELETE /api/reviews/{reviewId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reviewId` | 삭제할 강사평 ID입니다. |

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
| `200 OK` | `REVIEW_DELETED` | 강사평 삭제 성공 |

Response Body

```json
{
  "status": 200,
  "code": "REVIEW_DELETED",
  "message": "강사평 삭제 성공",
  "data": null
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `REVIEW_003` | 접근 권한이 없습니다. | 본인 강사평이 아닌 경우 |
| `404 Not Found` | `REVIEW_001` | 강사평을 찾을 수 없습니다. | 존재하지 않는 강사평인 경우 |

---

## 4. 강사평 요약 조회

`GET /api/trainer-profiles/{trainerProfileId}/reviews/summary`

트레이너 정보, 평균 별점, 별점 분포를 조회합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 불필요합니다. |

Path Variable

| name | description |
| --- | --- |
| `trainerProfileId` | 조회할 트레이너 프로필 ID입니다. |

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
| `200 OK` | `TRAINER_REVIEW_FETCHED` | 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "TRAINER_REVIEW_FETCHED",
  "message": "조회 성공",
  "data": {
    "trainerName": "홍길동",
    "introduction": "체형 교정과 근력 향상 전문입니다.",
    "averageRating": 4.8,
    "reviewCount": 127,
    "ratingDistribution": {
      "5": 100,
      "4": 20,
      "3": 5,
      "2": 1,
      "1": 1
    }
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.averageRating` | 평균 별점입니다. |
| `data.reviewCount` | 총 강사평 수입니다. |
| `data.ratingDistribution` | 별점별 강사평 수입니다. key는 1~5, value는 해당 별점 수입니다. |

---

## 5. 강사평 목록 조회

`GET /api/trainer-profiles/{trainerProfileId}/reviews?cursor=&cursorRating=&size=10&sort=LATEST`

커서 기반 페이지네이션으로 강사평 목록을 조회합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 불필요합니다. |

Path Variable

| name | description |
| --- | --- |
| `trainerProfileId` | 조회할 트레이너 프로필 ID입니다. |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `cursor` | X | 이전 응답의 `nextCursor`입니다. 첫 페이지는 미입력합니다. |
| `cursorRating` | X | `HIGH_RATING` 정렬 시 이전 응답의 `nextCursorRating`입니다. 첫 페이지는 미입력합니다. |
| `size` | X | 페이지 크기입니다. 기본값 `10`, 최대 `50`입니다. |
| `sort` | X | 정렬 기준입니다. `LATEST`(최신순, 기본값) 또는 `HIGH_RATING`(별점 높은 순) 중 하나입니다. |

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
| `200 OK` | `TRAINER_REVIEW_FETCHED` | 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "TRAINER_REVIEW_FETCHED",
  "message": "조회 성공",
  "data": {
    "reviews": [
      {
        "trainerReviewId": 1,
        "nickname": "수강생닉네임",
        "ptCourseTitle": "가슴 집중 PT",
        "rating": 5,
        "content": "정말 좋았습니다.",
        "createdAt": "2026-07-01T10:00:00",
        "isMine": true
      }
    ],
    "nextCursor": 1,
    "nextCursorRating": null,
    "hasNext": false
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.reviews[].isMine` | 로그인한 사용자가 작성한 강사평이면 `true`, 아니면 `false`. 비로그인 조회 시 항상 `false`입니다. |
| `data.nextCursor` | 다음 페이지 조회 시 `cursor` 파라미터로 전달합니다. 다음 페이지가 없으면 `null`입니다. |
| `data.nextCursorRating` | `HIGH_RATING` 정렬 시 다음 페이지 조회에 사용합니다. `LATEST` 정렬 시 `null`입니다. |
| `data.hasNext` | 다음 페이지 존재 여부입니다. |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
