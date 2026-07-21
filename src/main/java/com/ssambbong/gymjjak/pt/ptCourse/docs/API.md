# 🏋️ PT 강습 API

> 작성일: 2026-07-21
> 대상: `PtCourseController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 권한: 등록·수정·삭제는 `TRAINER`, 내 강습 목록·수강생 조회는 `TRAINER`, 목록·상세·통계·인기·예약 가능 정보는 인증이 불필요합니다.

## 기능 개요

- 🏋️ 트레이너는 PT 강습을 등록·수정·삭제·숨김 처리할 수 있습니다.
- 📋 누구나 PT 강습 목록, 상세, 인기 강습, 통계를 조회할 수 있습니다.
- 📅 예약 전 사용자는 예약 가능 날짜와 시간 슬롯을 확인할 수 있습니다.

---

## 1. PT 강습 등록

`POST /api/pt-courses`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Request Body

```json
{
  "title": "체계적인 가슴 집중 PT",
  "description": "가슴 근육 발달에 특화된 12주 프로그램입니다.",
  "part": "CHEST",
  "price": 50000,
  "organizationId": 1,
  "thumbnailFile": {
    "fileKey": "pt-courses/thumbnail.png",
    "originalName": "thumbnail.png",
    "contentType": "image/png",
    "fileSize": 204800
  },
  "curriculums": [
    { "sessionNo": 1, "title": "기초 자세 교정", "content": "현재 체력 및 목표 설정" }
  ],
  "schedules": [
    { "dayOfWeek": "MONDAY", "startTime": "10:00", "endTime": "11:00" }
  ]
}
```

| name | 필수 | 설명 |
| --- | --- | --- |
| `title` | O | PT 강습 제목입니다. |
| `description` | O | PT 강습 소개입니다. |
| `part` | O | 운동 부위입니다. `CHEST`, `BACK`, `SHOULDER`, `ARM`, `LEG`, `CORE`, `FULL_BODY` 등입니다. |
| `price` | O | 가격입니다. 0 이상이어야 합니다. |
| `organizationId` | O | 소속 조직 ID입니다. |
| `thumbnailFile` | X | S3 업로드 완료 후 전달하는 썸네일 파일 메타데이터입니다. |
| `curriculums` | O | 커리큘럼 목록입니다. 1개 이상이어야 합니다. |
| `curriculums[].sessionNo` | O | 회차 번호입니다. 1 이상이어야 합니다. |
| `curriculums[].title` | O | 회차 제목입니다. |
| `curriculums[].content` | X | 회차 설명입니다. |
| `schedules` | O | 수업 시간 목록입니다. 1개 이상이어야 합니다. |
| `schedules[].dayOfWeek` | O | 요일입니다. `MONDAY`~`SUNDAY` 중 하나입니다. |
| `schedules[].startTime` | O | 시작 시간입니다. `HH:mm` 형식입니다. |
| `schedules[].endTime` | O | 종료 시간입니다. `HH:mm` 형식입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `201 Created` | `PT_COURSE_201` | PT 강습 등록 성공 |

Response Body

```json
{
  "status": 201,
  "code": "PT_COURSE_201",
  "message": "PT 강습 등록 성공",
  "data": {
    "ptCourseId": 1
  }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `PT_COURSE_001` | PT 강습 정보가 유효하지 않습니다. | title/description null, price < 0 등 유효성 검증 실패인 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `PT_COURSE_004` | 선택한 조직에 소속되지 않은 트레이너입니다. | 요청한 조직에 소속되지 않은 트레이너인 경우 |
| `404 Not Found` | `PT_COURSE_005` | 트레이너 프로필을 찾을 수 없습니다. | 트레이너 프로필이 없는 경우 |

---

## 2. PT 강습 목록 조회

`GET /api/pt-courses`

`VISIBLE` 상태인 강습만 반환합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 불필요합니다. |

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
| `200 OK` | `PT_COURSE_200_LIST` | PT 강습 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_200_LIST",
  "message": "PT 강습 목록 조회 성공",
  "data": [
    {
      "ptCourseId": 1,
      "title": "크로스핏 초급 클래스",
      "thumbnailUrl": "https://...",
      "price": 45000,
      "part": "FULL_BODY",
      "trainerName": "홍길동",
      "organizationId": 1,
      "businessName": "짐짝피트니스 본점",
      "roadAddress": "서울시 강남구",
      "latitude": 37.5012,
      "longitude": 127.0396,
      "averageRating": 4.8,
      "reviewCount": 127,
      "createdAt": "2026-06-28T23:19:54"
    }
  ]
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data[].ptCourseId` | PT 강습 ID입니다. |
| `data[].thumbnailUrl` | 썸네일 이미지 URL입니다. |
| `data[].part` | 운동 부위입니다. |
| `data[].averageRating` | 트레이너 평균 별점입니다. |
| `data[].reviewCount` | 강사평 수입니다. |

---

## 3. PT 강습 상세 조회

`GET /api/pt-courses/{ptCourseId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 불필요합니다. |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | 조회할 PT 강습 ID입니다. |

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
| `200 OK` | `PT_COURSE_200_DETAIL` | PT 강습 상세 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_200_DETAIL",
  "message": "PT 강습 상세 조회 성공",
  "data": {
    "ptCourseId": 1,
    "thumbnailUrl": "https://...",
    "title": "맞춤 PT 1개월 과정",
    "description": "소개",
    "price": 345000,
    "totalSessionCount": 12,
    "organizationId": 1,
    "trainerProfileId": 1,
    "curriculums": [
      {
        "curriculumId": 1,
        "sessionNo": 1,
        "title": "기초 체력 평가 및 목표 설정",
        "content": "체력 측정 및 개인 목표 설정"
      }
    ],
    "schedules": [
      {
        "scheduleId": 1,
        "dayOfWeek": "MONDAY",
        "startTime": "10:00",
        "endTime": "11:00"
      }
    ],
    "recentReviews": [
      {
        "reviewId": 1,
        "rating": 5,
        "content": "정말 좋았습니다.",
        "createdAt": "2026-07-01T10:00:00"
      }
    ]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.totalSessionCount` | 전체 회차 수입니다. |
| `data.trainerProfileId` | 트레이너 프로필 ID입니다. |
| `data.curriculums` | 커리큘럼 목록입니다. |
| `data.schedules` | 수업 시간 목록입니다. |
| `data.recentReviews` | 최근 강사평 목록입니다. 최대 3개를 반환합니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `404 Not Found` | `PT_COURSE_002` | PT 강습을 찾을 수 없습니다. | 존재하지 않거나 VISIBLE 상태가 아닌 PT 강습인 경우 |

---

## 4. PT 강습 수정

`PATCH /api/pt-courses/{ptCourseId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | 수정할 PT 강습 ID입니다. |

Request Body

```json
{
  "title": "수정된 제목",
  "description": "수정된 설명",
  "part": "CHEST",
  "price": 60000,
  "thumbnailFile": null,
  "curriculums": [
    { "id": 1, "sessionNo": 1, "title": "수정된 회차 제목", "content": "수정된 내용" }
  ],
  "schedules": [
    { "id": 1, "dayOfWeek": "TUESDAY", "startTime": "14:00", "endTime": "15:00" }
  ]
}
```

| name | 설명 |
| --- | --- |
| `curriculums[].id` | 있으면 기존 커리큘럼 수정, 없으면 신규 생성합니다. |
| `schedules[].id` | 있으면 기존 스케줄 수정, 없으면 신규 생성합니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `PT_COURSE_UPDATED` | PT 강습 수정 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_UPDATED",
  "message": "PT 강습 수정 성공",
  "data": { "ptCourseId": 1 }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `CURRICULUM_UPDATE_NOT_ALLOWED` | 수강생이 있어 커리큘럼을 수정할 수 없습니다. | 이미 수강생이 있어 커리큘럼 변경이 불가한 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `PT_COURSE_003` | 본인의 PT 강습이 아닙니다. | 본인 강습이 아닌 경우 |
| `404 Not Found` | `PT_COURSE_002` | PT 강습을 찾을 수 없습니다. | 존재하지 않는 PT 강습인 경우 |

---

## 5. PT 강습 상태 변경

`PATCH /api/pt-courses/{ptCourseId}/status`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |
| `Content-Type` | `application/json` |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | 상태를 변경할 PT 강습 ID입니다. |

Request Body

```json
{ "status": "HIDDEN" }
```

| name | 설명 |
| --- | --- |
| `status` | 변경할 상태값입니다. `VISIBLE` 또는 `HIDDEN`만 설정할 수 있습니다. |

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `PT_COURSE_200_STATUS` | PT 강습 상태 변경 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_200_STATUS",
  "message": "PT 강습 상태 변경 성공",
  "data": null
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `PT_COURSE_006` | 트레이너는 VISIBLE 또는 HIDDEN만 설정할 수 있습니다. | `VISIBLE`/`HIDDEN` 이외의 상태값을 요청한 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `PT_COURSE_003` | 본인의 PT 강습이 아닙니다. | 본인 강습이 아닌 경우 |
| `404 Not Found` | `PT_COURSE_002` | PT 강습을 찾을 수 없습니다. | 존재하지 않는 PT 강습인 경우 |
| `409 Conflict` | `PT_COURSE_011` | 진행 중인 예약이 있어 PT 강습을 비활성화할 수 없습니다. | 활성 예약(`RESERVED`/`IN_PROGRESS`)이 있는 경우 |

---

## 6. PT 강습 삭제

`DELETE /api/pt-courses/{ptCourseId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | 삭제할 PT 강습 ID입니다. |

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
| `200 OK` | `PT_COURSE_DELETED` | PT 강습 삭제 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_DELETED",
  "message": "PT 강습 삭제 성공",
  "data": null
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `PT_COURSE_003` | 본인의 PT 강습이 아닙니다. | 본인 강습이 아닌 경우 |
| `404 Not Found` | `PT_COURSE_002` | PT 강습을 찾을 수 없습니다. | 존재하지 않는 PT 강습인 경우 |
| `409 Conflict` | `PT_COURSE_009` | BLOCKED 상태의 PT 강습은 삭제할 수 없습니다. | 신고로 인해 BLOCKED 상태인 경우 |
| `409 Conflict` | `PT_COURSE_010` | 진행 중인 예약이 있어 PT 강습을 삭제할 수 없습니다. | 활성 예약(`RESERVED`/`IN_PROGRESS`)이 있는 경우 |

---

## 7. 내 PT 강습 목록 조회

`GET /api/pt-courses/me?status=VISIBLE`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `status` | X | 강습 상태 필터입니다. `VISIBLE` 또는 `HIDDEN` 중 하나입니다. 미입력 시 전체를 반환합니다. |

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
| `200 OK` | `PT_COURSE_200_MY_LIST` | 내 강습 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_200_MY_LIST",
  "message": "내 강습 목록 조회 성공",
  "data": [
    {
      "ptCourseId": 1,
      "thumbnailUrl": "https://...",
      "title": "가슴 집중 PT",
      "trainerName": "홍길동",
      "status": "VISIBLE",
      "activeReservationCount": 3,
      "totalReservationCount": 10
    }
  ]
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data[].status` | 강습 상태입니다. `VISIBLE` 또는 `HIDDEN` 중 하나입니다. |
| `data[].activeReservationCount` | 현재 수강 중인 수강생 수입니다. |
| `data[].totalReservationCount` | 전체 수강생 수입니다. 취소된 예약은 제외합니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |

---

## 8. 강습별 수강생 목록 조회

`GET /api/pt-courses/{ptCourseId}/reservations`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | 조회할 PT 강습 ID입니다. |

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
| `200 OK` | `PT_COURSE_200_RESERVATIONS` | 수강생 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_200_RESERVATIONS",
  "message": "수강생 목록 조회 성공",
  "data": {
    "title": "가슴 집중 PT",
    "ptReservations": [
      {
        "ptReservationId": 1,
        "nickname": "수강생닉네임",
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
| `data.title` | PT 강습 제목입니다. |
| `data.ptReservations[].status` | 예약 상태입니다. `RESERVED` / `IN_PROGRESS` / `COMPLETED` / `CANCELLED` 중 하나입니다. |
| `data.ptReservations[].lastPtDate` | 마지막 PT 날짜입니다. |
| `data.ptReservations[].progressCount` | 완료된 세션 수입니다. |
| `data.ptReservations[].totalSessionCount` | 전체 회차 수입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `404 Not Found` | `PT_COURSE_002` | PT 강습을 찾을 수 없습니다. | 존재하지 않는 PT 강습인 경우 |

---

## 9. 수강생 상세 조회

`GET /api/pt-courses/reservations/{reservationId}`

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
| `200 OK` | `PT_COURSE_200_STUDENT_DETAIL` | 수강생 상세 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_200_STUDENT_DETAIL",
  "message": "수강생 상세 조회 성공",
  "data": {
    "nickname": "수강생닉네임",
    "email": "student@example.com",
    "phone": "010-1234-5678",
    "status": "IN_PROGRESS",
    "progressCount": 3,
    "totalSessionCount": 12,
    "title": "가슴 집중 PT"
  }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `404 Not Found` | `PT_COURSE_007` | 수강생 정보를 찾을 수 없습니다. | 존재하지 않는 수강생 정보인 경우 |

---

## 10. PT 통계 조회

`GET /api/pt-courses/stats`

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 불필요합니다. |

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
| `200 OK` | `PT_STATS` | PT 통계 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_STATS",
  "message": "PT 통계 조회 성공",
  "data": {
    "organizationCount": 25,
    "activeTrainerCount": 80,
    "inProgressPtCount": 310,
    "averageSatisfaction": 4.8
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.organizationCount` | 등록 헬스장 수입니다. |
| `data.activeTrainerCount` | 활동 트레이너 수입니다. |
| `data.inProgressPtCount` | 진행 중인 PT 수입니다. |
| `data.averageSatisfaction` | 평균 만족도입니다. 소수점 첫째 자리에서 반올림하며, 리뷰가 없으면 `null`입니다. |

---

## 11. 인기 강습 조회

`GET /api/pt-courses/popular`

예약 수 기준 상위 4개 `VISIBLE` 강습을 반환합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 불필요합니다. |

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
| `200 OK` | `PT_COURSE_POPULAR` | 인기 강습 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_POPULAR",
  "message": "인기 강습 조회 성공",
  "data": [
    {
      "ptCourseId": 1,
      "title": "가슴 집중 PT",
      "price": 50000,
      "thumbnailUrl": "https://...",
      "part": "CHEST",
      "trainerName": "홍길동",
      "roadAddress": "서울시 강남구"
    }
  ]
}
```

---

## 12. 예약 가능 날짜 조회

`GET /api/pt-courses/{ptCourseId}/available-dates`

오늘부터 30일 이내 예약 가능한 날짜 목록을 반환합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 불필요합니다. |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | 조회할 PT 강습 ID입니다. |

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
| `200 OK` | `PT_COURSE_200_AVAILABLE_DATES` | 예약 가능 날짜 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_200_AVAILABLE_DATES",
  "message": "예약 가능 날짜 조회 성공",
  "data": {
    "availableDates": ["2026-07-21", "2026-07-22", "2026-07-28"]
  }
}
```

---

## 13. 예약 가능 시간 슬롯 조회

`GET /api/pt-courses/{ptCourseId}/available-time-slots?date=2026-07-21`

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 불필요합니다. |

Path Variable

| name | description |
| --- | --- |
| `ptCourseId` | 조회할 PT 강습 ID입니다. |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `date` | O | 조회할 날짜입니다. `yyyy-MM-dd` 형식입니다. |

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
| `200 OK` | `PT_COURSE_200_AVAILABLE_TIME_SLOTS` | 예약 가능 시간 슬롯 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "PT_COURSE_200_AVAILABLE_TIME_SLOTS",
  "message": "예약 가능 시간 슬롯 조회 성공",
  "data": {
    "date": "2026-07-21",
    "timeSlots": [
      { "startTime": "10:00", "endTime": "11:00", "available": true },
      { "startTime": "14:00", "endTime": "15:00", "available": false }
    ]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.timeSlots[].available` | 예약 가능 여부입니다. `false`이면 이미 예약된 슬롯입니다. |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
