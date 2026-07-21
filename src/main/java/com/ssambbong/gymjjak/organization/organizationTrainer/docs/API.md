# 👥 조직 소속 트레이너 API

> 작성일: 2026-07-21
> 대상: `OrganizationTrainerController`, `TrainerOrganizationController`
> 공통 응답 형식: `status`, `code`, `message`, `data`

## 기능 개요

- 조직 계정이 본인 조직에 소속된 트레이너를 조회·추가·삭제합니다.
- 트레이너 계정이 본인이 소속된 조직 목록을 조회합니다.

---

## 1. 내 조직 소속 트레이너 목록 조회

`GET /api/organizations/me/trainers`

> 🔒 ORGANIZATION 권한 필요.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ORGANIZATION 권한) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_OT_200_L` | 소속 트레이너 목록 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_OT_200_L",
  "message": "소속 트레이너 목록 조회가 완료되었습니다.",
  "data": {
    "trainers": [
      {
        "organizationTrainerId": 1,
        "trainerProfileId": 10,
        "username": "trainer01@example.com",
        "nickname": "트레이너닉네임",
        "trainerName": "김트레이너",
        "registeredAt": "2026-07-05T09:00:00"
      }
    ]
  }
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data.trainers[].organizationTrainerId` | 소속 트레이너 관계 ID |
| `data.trainers[].trainerProfileId` | 트레이너 프로필 ID |
| `data.trainers[].username` | 트레이너 계정 로그인 ID |
| `data.trainers[].nickname` | 트레이너 계정 닉네임 |
| `data.trainers[].trainerName` | 트레이너 프로필 이름 |
| `data.trainers[].registeredAt` | 소속 등록 일시 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ORGANIZATION 권한 없음 |
| `404 Not Found` | `ORG_ORG_404` | 조직을 찾을 수 없습니다. | 조직 계정에 연결된 조직 없음 |

---

## 2. 소속 트레이너 추가

`POST /api/organizations/me/trainers`

> 🔒 ORGANIZATION 권한 필요.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ORGANIZATION 권한) |

Request Body

```json
{
  "trainerProfileId": 10
}
```

| name | 필수 | description |
| --- | --- | --- |
| `trainerProfileId` | O | 추가할 트레이너의 프로필 ID (양수) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `201 Created` | `ORG_OT_201` | 소속 트레이너 등록이 완료되었습니다. |

Response Body

```json
{
  "status": 201,
  "code": "ORG_OT_201",
  "message": "소속 트레이너 등록이 완료되었습니다.",
  "data": {
    "organizationTrainerId": 1
  }
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | `trainerProfileId` 누락 또는 0 이하 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ORGANIZATION 권한 없음 |
| `404 Not Found` | `ORG_ORG_404` | 조직을 찾을 수 없습니다. | 조직 계정에 연결된 조직 없음 |
| `409 Conflict` | `ORG_OT_409` | 이미 소속된 트레이너입니다. | 동일 트레이너가 이미 소속 중 |

---

## 3. 소속 트레이너 삭제

`DELETE /api/organizations/me/trainers/{organizationTrainerId}`

> 🔒 ORGANIZATION 권한 필요.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ORGANIZATION 권한) |

Path Variable

| name | description |
| --- | --- |
| `organizationTrainerId` | 삭제할 소속 트레이너 관계 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_OT_200_D` | 소속 트레이너 삭제가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_OT_200_D",
  "message": "소속 트레이너 삭제가 완료되었습니다.",
  "data": null
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ORGANIZATION 권한 없음 |
| `404 Not Found` | `ORG_ORG_404` | 조직을 찾을 수 없습니다. | 조직 계정에 연결된 조직 없음 |
| `404 Not Found` | `ORG_OT_404` | 소속 트레이너를 찾을 수 없습니다. | 본인 조직 소속이 아니거나 이미 삭제됨 |

---

## 4. 내 소속 조직 목록 조회 (트레이너)

`GET /api/organizations/trainer/my-organizations`

> 🔒 TRAINER 권한 필요. 트레이너가 소속된 조직 목록을 조회합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (TRAINER 권한) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_OT_200_MY_ORGS` | 소속 조직 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "ORG_OT_200_MY_ORGS",
  "message": "소속 조직 목록 조회 성공",
  "data": [
    {
      "organizationId": 1,
      "businessName": "짐짝피트니스",
      "roadAddress": "서울시 강남구 테헤란로 123"
    }
  ]
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `[].organizationId` | 조직 ID |
| `[].businessName` | 상호명 |
| `[].roadAddress` | 도로명 주소 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | TRAINER 권한 없음 |
| `404 Not Found` | `ORG_OT_404` | 소속 트레이너를 찾을 수 없습니다. | 트레이너 프로필 없음 또는 소속 조직 없음 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
