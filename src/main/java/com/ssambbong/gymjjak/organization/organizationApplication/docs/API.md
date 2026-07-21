# 🏢 조직 신청 API

> 작성일: 2026-07-21
> 대상: `OrganizationApplicationController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 기본 경로: `/api/organization-applications`

## 기능 개요

- 사용자가 헬스장(조직) 등록을 신청합니다.
- 신청 시 사업자등록증 파일을 함께 업로드합니다.
- 관리자가 신청을 검토하여 승인 또는 반려합니다.
- 승인 시 조직 계정과 조직 정보가 자동으로 생성됩니다.

---

## 1. 조직 신청

`POST /api/organization-applications`

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` |
| `Content-Type` | `application/json` |

Request Body

```json
{
  "businessLicenseFile": {
    "fileKey": "business-license/uuid-filename.pdf",
    "originalName": "사업자등록증.pdf",
    "contentType": "application/pdf",
    "fileSize": 204800
  },
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
  "websiteUrl": "https://gymjjak.com",
  "instagramUrl": "https://instagram.com/gymjjak",
  "blogUrl": "https://blog.naver.com/gymjjak",
  "facilityPhone": "02-1234-5678"
}
```

| name | 필수 | description |
| --- | --- | --- |
| `businessLicenseFile` | O | S3에 업로드된 사업자등록증 파일 메타데이터 |
| `businessLicenseFile.fileKey` | O | S3 오브젝트 키 |
| `businessLicenseFile.originalName` | O | 원본 파일명 |
| `businessLicenseFile.contentType` | O | MIME 타입 |
| `businessLicenseFile.fileSize` | O | 파일 크기 (bytes) |
| `requestedLoginId` | O | 조직 계정으로 사용할 로그인 ID (이메일 형식) |
| `businessRegistrationNumber` | O | 사업자등록번호 (10자리 숫자) |
| `businessName` | O | 상호명 |
| `representativeName` | O | 대표자명 |
| `representativePhone` | O | 대표자 연락처 (형식: `010-0000-0000`) |
| `openingDate` | O | 개업일자 (현재 또는 과거) |
| `roadAddress` | O | 도로명 주소 |
| `jibunAddress` | X | 지번 주소 |
| `detailAddress` | X | 상세 주소 |
| `latitude` | X | 위도 |
| `longitude` | X | 경도 |
| `websiteUrl` | X | 웹사이트 URL |
| `instagramUrl` | X | 인스타그램 URL |
| `blogUrl` | X | 블로그 URL |
| `facilityPhone` | X | 시설 대표 전화번호 |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `201 Created` | `ORG_201` | 조직 신청이 완료되었습니다. |

Response Body

```json
{
  "status": 201,
  "code": "ORG_201",
  "message": "조직 신청이 완료되었습니다.",
  "data": {
    "organizationApplicationId": 1
  }
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | 필수값 누락 또는 형식 오류 |
| `400 Bad Request` | `ORG_400` | 사업자등록증 파일은 필수입니다. | 파일 메타데이터 누락 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `409 Conflict` | `ORG_001` | 이미 등록된 사업자 번호입니다. | 동일 사업자번호 PENDING 신청 존재 |
| `409 Conflict` | `ORG_002` | 이미 사용 중인 로그인 ID입니다. | 동일 로그인 ID 신청 존재 |
| `500 Internal Server Error` | `ORG_500` | 사업자등록증 파일 등록에 실패했습니다. | S3 파일 등록 실패 |

---

## 2. 내 조직 신청 목록 조회

`GET /api/organization-applications/me`

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_200` | 조직 신청 목록 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_200",
  "message": "조직 신청 목록 조회가 완료되었습니다.",
  "data": [
    {
      "organizationApplicationId": 1,
      "businessName": "짐짝피트니스",
      "requestedLoginId": "gymjjak@example.com",
      "status": "PENDING",
      "businessRegistrationNumber": "1234567890",
      "representativeName": "홍길동",
      "createdAt": "2026-07-21T10:00:00"
    }
  ]
}
```

#### Response Field

| name | 설명 |
| --- | --- |
| `[].organizationApplicationId` | 신청 ID |
| `[].businessName` | 상호명 |
| `[].requestedLoginId` | 신청한 로그인 ID |
| `[].status` | 신청 상태. `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED` |
| `[].businessRegistrationNumber` | 사업자등록번호 |
| `[].representativeName` | 대표자명 |
| `[].createdAt` | 신청 일시 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |

---

## 3. 관리자 조직 신청 목록 조회

`GET /api/organization-applications?page=0&size=10`

> 🔒 ADMIN 권한 필요. PENDING 상태 신청만 조회됩니다.

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

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_200` | 조직 신청 전체 목록 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_200",
  "message": "조직 신청 전체 목록 조회가 완료되었습니다.",
  "data": {
    "applications": [
      {
        "organizationApplicationId": 1,
        "requestedLoginId": "gymjjak@example.com",
        "businessName": "짐짝피트니스",
        "representativeName": "홍길동",
        "representativePhone": "010-1234-5678"
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
| `data.applications[].organizationApplicationId` | 신청 ID |
| `data.applications[].requestedLoginId` | 신청한 로그인 ID |
| `data.applications[].businessName` | 상호명 |
| `data.applications[].representativeName` | 대표자명 |
| `data.applications[].representativePhone` | 대표자 연락처 |
| `data.page` | 현재 페이지 번호 |
| `data.size` | 페이지 크기 |
| `data.totalElements` | 전체 신청 수 |
| `data.totalPages` | 전체 페이지 수 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ADMIN 권한 없음 |

---

## 4. 조직 신청 상세 조회

`GET /api/organization-applications/{applicationId}`

> ADMIN은 모든 신청 조회 가능. 일반 사용자는 본인 신청만 조회 가능.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` |

Path Variable

| name | description |
| --- | --- |
| `applicationId` | 조회할 신청 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_200` | 조직 신청 상세 조회가 완료되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_200",
  "message": "조직 신청 상세 조회가 완료되었습니다.",
  "data": {
    "organizationApplicationId": 1,
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
    "websiteUrl": "https://gymjjak.com",
    "instagramUrl": "https://instagram.com/gymjjak",
    "blogUrl": "https://blog.naver.com/gymjjak",
    "facilityPhone": "02-1234-5678",
    "businessLicenseFileUrl": "https://s3.ap-northeast-2.amazonaws.com/...",
    "businessLicenseOriginalName": "사업자등록증.pdf"
  }
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `ORG_403` | 해당 조직 신청에 접근할 권한이 없습니다. | 본인 신청이 아닌 경우 |
| `404 Not Found` | `ORG_404` | 조직 신청 내역을 찾을 수 없습니다. | 존재하지 않는 신청 ID |

---

## 5. 조직 신청 승인

`PATCH /api/organization-applications/{applicationId}/approve`

> 🔒 ADMIN 권한 필요. 승인 시 조직 계정 및 조직 정보가 자동 생성됩니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ADMIN 권한) |

Path Variable

| name | description |
| --- | --- |
| `applicationId` | 승인할 신청 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_200` | 조직 신청이 승인되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_200",
  "message": "조직 신청이 승인되었습니다.",
  "data": null
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ADMIN 권한 없음 |
| `404 Not Found` | `ORG_404` | 조직 신청 내역을 찾을 수 없습니다. | 존재하지 않는 신청 ID |
| `409 Conflict` | `ORG_409_1` | 이미 승인 처리된 신청입니다. | 이미 승인된 신청 |
| `409 Conflict` | `ORG_409_2` | 이미 반려 처리된 신청입니다. | 이미 반려된 신청 |
| `409 Conflict` | `ORG_409_3` | 이미 취소 처리된 신청입니다. | 이미 취소된 신청 |

---

## 6. 조직 신청 반려

`PATCH /api/organization-applications/{applicationId}/reject`

> 🔒 ADMIN 권한 필요.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` (ADMIN 권한) |

Path Variable

| name | description |
| --- | --- |
| `applicationId` | 반려할 신청 ID |

Request Body

```json
{
  "rejectReason": "사업자등록증 정보가 일치하지 않습니다."
}
```

| name | 필수 | description |
| --- | --- | --- |
| `rejectReason` | O | 반려 사유 (공백 불가) |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_200` | 조직 신청이 반려되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_200",
  "message": "조직 신청이 반려되었습니다.",
  "data": null
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | `rejectReason` 누락 또는 공백 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ADMIN 권한 없음 |
| `404 Not Found` | `ORG_404` | 조직 신청 내역을 찾을 수 없습니다. | 존재하지 않는 신청 ID |
| `409 Conflict` | `ORG_409_1` | 이미 승인 처리된 신청입니다. | 이미 승인된 신청 |
| `409 Conflict` | `ORG_409_2` | 이미 반려 처리된 신청입니다. | 이미 반려된 신청 |
| `409 Conflict` | `ORG_409_3` | 이미 취소 처리된 신청입니다. | 이미 취소된 신청 |

---

## 7. 조직 신청 취소

`PATCH /api/organization-applications/{applicationId}/cancel`

> 본인 신청만 취소 가능. PENDING 상태일 때만 취소 가능합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` |

Path Variable

| name | description |
| --- | --- |
| `applicationId` | 취소할 신청 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_200` | 조직 신청이 취소되었습니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_200",
  "message": "조직 신청이 취소되었습니다.",
  "data": null
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `404 Not Found` | `ORG_404` | 조직 신청 내역을 찾을 수 없습니다. | 본인 신청이 아니거나 존재하지 않는 ID |
| `409 Conflict` | `ORG_409_1` | 이미 승인 처리된 신청입니다. | 이미 승인된 신청 |
| `409 Conflict` | `ORG_409_2` | 이미 반려 처리된 신청입니다. | 이미 반려된 신청 |
| `409 Conflict` | `ORG_409_3` | 이미 취소 처리된 신청입니다. | 이미 취소된 신청 |

---

## 8. 로그인 ID 중복 확인

`GET /api/organization-applications/login-id/duplicate?requestedLoginId=gymjjak@example.com`

> 신청 테이블 기준으로만 중복을 확인합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `requestedLoginId` | O | 중복 확인할 로그인 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `ORG_200` | 사용 가능한 ID입니다. |

Response Body

```json
{
  "status": 200,
  "code": "ORG_200",
  "message": "사용 가능한 ID입니다.",
  "data": null
}
```

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | `requestedLoginId` 누락 또는 공백 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `409 Conflict` | `ORG_002` | 이미 사용 중인 로그인 ID입니다. | 동일 로그인 ID 신청 존재 |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
