# 🧑‍🏫 트레이너 신청 API

## ✅ 최신 사용자 신청 조회 API

> 업데이트: 2026-07-18 · 다중 조직 신청 구조에 맞춰 사용자 조회 API를 목록·상세로 분리했습니다.

### 1. 내 트레이너 신청 목록 조회

`GET /api/trainer-applications/me?page=0`  
권한: `USER`, `TRAINER`

- Request Body는 사용하지 않습니다.
- `page`의 기본값은 `0`이며, 페이지 크기는 `10`건으로 고정됩니다.
- 조직명은 신청서와 조직 정보를 조인한 단일 조회로 반환합니다.
- 신청서가 없으면 `200 OK`와 빈 `content` 배열을 반환합니다.

#### Response · `200 OK`

```json
{
  "status": 200,
  "code": "TRAINER_APPLICATION_200_3",
  "message": "트레이너 신청 목록 조회에 성공했습니다.",
  "data": {
    "content": [{
      "trainerApplicationId": 101,
      "organizationName": "짐짝 피트니스",
      "status": "PENDING",
      "createdAt": "2026-07-18T01:34:30",
      "reviewedAt": null,
      "rejectReason": null
    }],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1,
    "hasNext": false
  }
}
```

### 2. 내 트레이너 신청 상세 조회

`GET /api/trainer-applications/me/{trainerApplicationId}`  
권한: `USER`, `TRAINER`

- Request Body는 사용하지 않습니다.
- 로그인한 사용자의 ID와 신청서 ID를 함께 조건으로 조회합니다.
- 신청서가 없거나 본인 소유가 아니면 `404 Not Found`를 반환합니다.
- 기존 상세 응답의 파일 URL, 자격증, 경력, 소개, 심사 정보를 그대로 반환합니다.

---

> 기준일: 2026-07-18  
> 공통 응답 형식: `status`, `code`, `message`, `data`

## 📌 사용자 API

### 1. 다중 조직 트레이너 신청 생성

`POST /api/trainer-applications`  
권한: `USER`

선택한 조직마다 독립된 신청 행을 생성합니다. 파일 등록과 OCR 검증은 요청당 한 번만 실행됩니다.

#### Request

```json
{
  "organizationIds": [1, 2, 3],
  "profileImageFile": {
    "fileKey": "trainer-applications/profile.png",
    "originalName": "profile.png",
    "contentType": "image/png",
    "fileSize": 1024
  },
  "certificateFile": {
    "fileKey": "trainer-applications/certificate.pdf",
    "originalName": "certificate.pdf",
    "contentType": "application/pdf",
    "fileSize": 2048
  },
  "qualifications": ["NSCA-CPT"],
  "awardHistories": ["2025 피트니스 대회 입상"],
  "introduction": "체형 교정과 근력 향상을 전문으로 합니다."
}
```

#### Response · `201 Created`

```json
{
  "status": 201,
  "code": "TRAINER_APPLICATION_201_1",
  "message": "...",
  "data": {
    "trainerApplicationIds": [101, 102, 103]
  }
}
```

#### 검증 및 정책

- `organizationIds`는 하나 이상이어야 하며 null, 0 이하 값, 중복 값을 포함할 수 없습니다.
- 대상 조직은 모두 활성 상태여야 합니다.
- 같은 사용자는 같은 조직에 `PENDING` 또는 `APPROVED` 상태 신청을 중복 제출할 수 없습니다.
- 한 조직에서 승인 또는 반려되어도 다른 조직의 신청 상태는 변경되지 않습니다.

---

### 2. 트레이너 신청 수정

`PATCH /api/trainer-applications/{trainerApplicationId}`  
권한: `USER`

지정한 단일 신청 건을 수정합니다. 본인의 `PENDING` 신청만 수정할 수 있으며, 필수 자격증 파일은 변경할 수 없습니다.

#### Request

```json
{
  "profileImageAction": "KEEP",
  "profileImageFile": null,
  "qualifications": ["NSCA-CPT", "ACSM-CPT"],
  "awardHistories": ["2025 피트니스 대회 입상"],
  "introduction": "수정된 자기소개입니다."
}
```

`profileImageAction`: `KEEP` · `REPLACE` · `DELETE`

#### Response · `201 Created`

```json
{
  "status": 201,
  "code": "TRAINER_APPLICATION_201_2",
  "message": "...",
  "data": {
    "trainerApplicationId": 101
  }
}
```

---

### 3. 트레이너 신청 취소

`DELETE /api/trainer-applications/{trainerApplicationId}`  
권한: `USER`

본인의 `PENDING` 신청 한 건을 취소합니다.

#### Response · `200 OK`

```json
{
  "status": 200,
  "code": "TRAINER_APPLICATION_200_7",
  "message": "...",
  "data": null
}
```

---

## 🏢 조직 심사 API

### 4. 조직별 신청 목록 조회

`GET /api/trainer-applications?status=PENDING&keyword=&page=0&size=20`  
권한: `ORGANIZATION`

로그인한 조직에 제출된 신청만 페이지 단위로 조회합니다.

- `status`: 미지정 시 `PENDING`
- `keyword`: username, name, nickname 검색
- `page`: 0부터 시작
- `size`: 1~100, 미지정 시 20

### 5. 조직 심사용 신청 상세 조회

`GET /api/trainer-applications/{trainerApplicationId}`  
권한: `ORGANIZATION`

해당 조직에 제출된 신청인지 검증한 뒤, 파일 URL·자격증·수상 이력·자기소개를 반환합니다.

### 6. 신청 승인

`PATCH /api/trainer-applications/{trainerApplicationId}/approve`  
권한: `ORGANIZATION`

`PENDING` 신청을 승인합니다. 최초 승인에서는 트레이너 프로필을 생성하고, 이후 조직 승인에서는 기존 프로필을 사용해 조직 소속만 추가합니다.

### 7. 신청 반려

`PATCH /api/trainer-applications/{trainerApplicationId}/reject`  
권한: `ORGANIZATION`

#### Request

```json
{
  "rejectReason": "필수 서류를 확인할 수 없습니다."
}
```

---

## ⚠️ 주요 오류

| HTTP | 상황 |
| --- | --- |
| `400` | 요청 값 검증 실패, 비활성 조직 선택, OCR 검증 실패 |
| `401` | 인증 실패 |
| `403` | 역할 부족 또는 신청 소유자/조직 불일치 |
| `404` | 신청서를 찾을 수 없음 |
| `409` | 중복 신청, `PENDING` 이외 상태의 수정·취소·심사 시도 |
