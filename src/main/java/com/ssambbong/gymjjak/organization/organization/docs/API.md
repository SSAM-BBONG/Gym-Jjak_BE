# 🏢 조직 API

> 작성일: 2026-07-21
> 대상: `OrganizationController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 기본 경로: `/api/organizations`

## 기능 개요

- 관리자가 전체 조직 목록·상세를 조회합니다.
- 일반 사용자·트레이너가 특정 조직의 공개 정보를 조회합니다.
- 사용자·트레이너가 소속 신청 전 상호명 또는 대표자명으로 조직을 검색합니다.
- 조직 계정이 본인 조직 정보를 조회하고 수정합니다.

---

## 1. 조직 목록 조회 (관리자)

`GET /api/organizations?page=0&size=10&keyword=짐짝`

> 🔒 ADMIN 권한 필요.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ADMIN 권한) |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `page` | X | 페이지 번호 (0부터 시작, 기본값: 0) |
| `size` | X | 페이지 크기 (기본값: 10, 최대: 100) |
| `keyword` | X | 상호명 또는 대표자명 검색어 |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_ORG_200_L` | 조직 목록 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_ORG_200_L",
  "message": "조직 목록 조회가 완료되었습니다.",
  "data": {
    "organizations": [
      {
        "organizationId": 1,
        "loginId": "gymjjak@example.com",
        "businessName": "짐짝피트니스",
        "representativeName": "홍길동",
        "representativePhone": "010-1234-5678",
        "trainerCount": 3,
        "status": "ACTIVE",
        "createdAt": "2026-07-01T10:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data.organizations[].organizationId` | 조직 ID |
| `data.organizations[].loginId` | 조직 계정 로그인 ID |
| `data.organizations[].businessName` | 상호명 |
| `data.organizations[].representativeName` | 대표자명 |
| `data.organizations[].representativePhone` | 대표자 연락처 |
| `data.organizations[].trainerCount` | 소속 트레이너 수 |
| `data.organizations[].status` | 조직 상태. `ACTIVE`, `INACTIVE` 등 |
| `data.organizations[].createdAt` | 조직 생성 일시 |
| `data.page` | 현재 페이지 번호 |
| `data.size` | 페이지 크기 |
| `data.totalElements` | 전체 조직 수 |
| `data.totalPages` | 전체 페이지 수 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ADMIN 권한 없음 |

---

## 2. 조직 상세 조회 (관리자)

`GET /api/organizations/{organizationId}`

> 🔒 ADMIN 권한 필요. 사업자등록증 파일 URL 및 소속 트레이너 목록 포함.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ADMIN 권한) |

Path Variable

| name | description |
| --- | --- |
| `organizationId` | 조회할 조직 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_ORG_200` | 조직 정보 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_ORG_200",
  "message": "조직 정보 조회가 완료되었습니다.",
  "data": {
    "organizationId": 1,
    "requestedLoginId": "gymjjak@example.com",
    "businessLicenseFileUrl": "https://s3.ap-northeast-2.amazonaws.com/...",
    "businessLicenseOriginalName": "사업자등록증.pdf",
    "businessRegistrationNumber": "1234567890",
    "businessName": "짐짝피트니스",
    "representativeName": "홍길동",
    "representativePhone": "010-1234-5678",
    "openingDate": "2024-01-01",
    "roadAddress": "서울시 강남구 테헤란로 123",
    "detailAddress": "1층",
    "latitude": 37.5012,
    "longitude": 127.0396,
    "facilityPhone": "02-1234-5678",
    "instagramUrl": "https://instagram.com/gymjjak",
    "blogUrl": "https://blog.naver.com/gymjjak",
    "websiteUrl": "https://gymjjak.com",
    "status": "ACTIVE",
    "approvedAt": "2026-07-01T10:00:00",
    "trainerCount": 2,
    "trainers": [
      {
        "trainerName": "김트레이너",
        "email": "trainer@example.com",
        "registeredAt": "2026-07-05T09:00:00"
      }
    ]
  }
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ADMIN 권한 없음 |
| `404 Not Found` | `ORG_ORG_404` | 조직을 찾을 수 없습니다. | 존재하지 않는 조직 ID |

---

## 3. 조직 상세 조회 (사용자)

`GET /api/organizations/{organizationId}/detail`

> 인증 없이 접근 가능. 공개 정보만 반환합니다.

### **[request]**

Path Variable

| name | description |
| --- | --- |
| `organizationId` | 조회할 조직 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_ORG_200_D` | 조직 상세 정보 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_ORG_200_D",
  "message": "조직 상세 정보 조회가 완료되었습니다.",
  "data": {
    "businessName": "짐짝피트니스",
    "roadAddress": "서울시 강남구 테헤란로 123",
    "detailAddress": "1층",
    "facilityPhone": "02-1234-5678",
    "instagramUrl": "https://instagram.com/gymjjak",
    "blogUrl": "https://blog.naver.com/gymjjak",
    "websiteUrl": "https://gymjjak.com",
    "trainerCount": 2,
    "avgRating": 4.5,
    "accumulatedMembers": 128,
    "trainers": [
      {
        "trainerName": "김트레이너",
        "averageRating": 4.8,
        "reviewCount": 20
      }
    ]
  }
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `data.avgRating` | 소속 트레이너 평균 평점 (소속 트레이너 없으면 0.0) |
| `data.accumulatedMembers` | 소속 트레이너들의 누적 회원 수 합산 |
| `data.trainers[].trainerName` | 트레이너 이름 |
| `data.trainers[].averageRating` | 트레이너 평균 평점 |
| `data.trainers[].reviewCount` | 트레이너 리뷰 수 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `404 Not Found` | `ORG_ORG_404` | 조직을 찾을 수 없습니다. | 존재하지 않는 조직 ID |

---

## 4. 내 조직 정보 조회

`GET /api/organizations/me`

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
| `200 OK` | `ORG_ORG_200` | 조직 정보 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_ORG_200",
  "message": "조직 정보 조회가 완료되었습니다.",
  "data": {
    "requestedLoginId": "gymjjak@example.com",
    "businessRegistrationNumber": "1234567890",
    "businessName": "짐짝피트니스",
    "representativeName": "홍길동",
    "representativePhone": "010-1234-5678",
    "openingDate": "2024-01-01",
    "roadAddress": "서울시 강남구 테헤란로 123",
    "jibunAddress": "서울시 강남구 역삼동 123-45",
    "detailAddress": "1층",
    "latitude": 37.5012,
    "longitude": 127.0396,
    "businessLicenseFileUrl": "https://s3.ap-northeast-2.amazonaws.com/...",
    "businessLicenseOriginalName": "사업자등록증.pdf",
    "facilityPhone": "02-1234-5678",
    "instagramUrl": "https://instagram.com/gymjjak",
    "blogUrl": "https://blog.naver.com/gymjjak",
    "websiteUrl": "https://gymjjak.com"
  }
}
```

#### Response Field

| name | 수정 가능 여부 | 설명 |
| --- | --- | --- |
| `requestedLoginId` ~ `longitude` | ❌ | 신청 당시 확정된 기본 정보 |
| `businessLicenseFileUrl`, `businessLicenseOriginalName` | ❌ | 사업자등록증 |
| `facilityPhone`, `instagramUrl`, `blogUrl`, `websiteUrl` | ✅ | `PATCH /api/organizations/me`로 수정 가능 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ORGANIZATION 권한 없음 |
| `404 Not Found` | `ORG_ORG_404` | 조직을 찾을 수 없습니다. | 조직 계정에 연결된 조직 없음 |

---

## 5. 조직 검색

`GET /api/organizations/search?keyword=짐짝&page=0&size=10`

> 🔒 USER 또는 TRAINER 권한 필요. 소속 신청 전 조직을 찾을 때 사용합니다.
> keyword가 없거나 공백이면 빈 목록을 반환합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (USER 또는 TRAINER 권한) |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `keyword` | X | 상호명 또는 대표자명 검색어. 없으면 빈 목록 반환 |
| `page` | X | 페이지 번호 (0부터 시작, 기본값: 0) |
| `size` | X | 페이지 크기 (기본값: 10, 최대: 100) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_ORG_200_S` | 조직 검색이 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_ORG_200_S",
  "message": "조직 검색이 완료되었습니다.",
  "data": {
    "content": [
      {
        "organizationId": 1,
        "businessName": "짐짝피트니스",
        "representativeName": "홍길동",
        "roadAddress": "서울시 강남구 테헤란로 123",
        "detailAddress": "1층"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | USER/TRAINER 권한 없음 |

---

## 6. 내 조직 정보 수정

`PATCH /api/organizations/me`

> 🔒 ORGANIZATION 권한 필요. 수정 가능한 추가 정보(시설 연락처, SNS 링크 등)만 변경합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ORGANIZATION 권한) |

Request Body

```json
{
  "facilityPhone": "02-9876-5432",
  "instagramUrl": "https://instagram.com/gymjjak_new",
  "blogUrl": "https://blog.naver.com/gymjjak_new",
  "websiteUrl": "https://new.gymjjak.com"
}
```

| name | 필수 | description |
| --- | --- | --- |
| `facilityPhone` | X | 시설 대표 전화번호 (형식: `02-0000-0000`) |
| `instagramUrl` | X | 인스타그램 URL |
| `blogUrl` | X | 블로그 URL |
| `websiteUrl` | X | 웹사이트 URL |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_ORG_200_U` | 조직 정보 수정이 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_ORG_200_U",
  "message": "조직 정보 수정이 완료되었습니다.",
  "data": null
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | 전화번호 형식 오류 등 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ORGANIZATION 권한 없음 |
| `404 Not Found` | `ORG_ORG_404` | 조직을 찾을 수 없습니다. | 조직 계정에 연결된 조직 없음 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
