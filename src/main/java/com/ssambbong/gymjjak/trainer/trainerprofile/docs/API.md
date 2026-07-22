# 🏋️ 트레이너 메인 페이지 대시보드 API

## 1. 트레이너 메인 페이지 조회

`GET /api/dashboard/trainer/main`  
권한: `TRAINER`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` · `TRAINER` 권한의 Access Token |

Request Parameter

없음

Request Body

없음

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 트레이너 메인 페이지 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "TRAINER_PROFILE_200_5",
  "message": "트레이너 메인 페이지 조회에 성공했습니다.",
  "data": {
    "organizationCount": 2,
    "currentStudentCount": 12,
    "averageRating": 4.8,
    "reviewCount": 15,
    "inProgressPtCourses": [
      {
        "ptCourseId": 101,
        "thumbnailUrl": "https://example.com/pt-thumbnail.jpg",
        "title": "체형 교정 PT",
        "trainerName": "홍길동",
        "organizationName": "짐잭 강남점",
        "price": 70000,
        "currentStudentCount": 5
      }
    ]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `status` | HTTP 상태 코드입니다. |
| `code` | 서비스 응답 코드입니다. |
| `message` | 응답 메시지입니다. |
| `data.organizationCount` | `removedAt`이 없는 활성 소속 조직 수입니다. 같은 조직 관계가 중복되어도 한 번만 집계합니다. |
| `data.currentStudentCount` | 내 `VISIBLE`, `HIDDEN` PT 강습의 `IN_PROGRESS` 수강생을 강습별로 `DISTINCT userId` 집계한 합계입니다. 동일 수강생이 서로 다른 강습을 수강하면 강습별로 각각 집계합니다. |
| `data.averageRating` | 트레이너 프로필에 저장된 평균 강사 평점입니다. 리뷰가 없으면 `0`입니다. |
| `data.reviewCount` | 트레이너 프로필에 저장된 강사평 수입니다. 리뷰가 없으면 `0`입니다. |
| `data.inProgressPtCourses` | 내 `VISIBLE`, `HIDDEN` PT 강습 목록입니다. 현재 수강생이 없는 강습도 포함하며 최대 4개를 반환합니다. |
| `data.inProgressPtCourses[].ptCourseId` | PT 강습 상세 이동에 사용하는 ID입니다. |
| `data.inProgressPtCourses[].thumbnailUrl` | PT 썸네일 URL입니다. 썸네일이 없거나 URL 조회에 실패하면 `null`입니다. |
| `data.inProgressPtCourses[].title` | PT 강습명입니다. |
| `data.inProgressPtCourses[].trainerName` | 담당 트레이너명입니다. |
| `data.inProgressPtCourses[].organizationName` | 강습이 속한 조직명입니다. 소속 조직 ID가 없거나 조직명을 찾지 못하면 `null`입니다. |
| `data.inProgressPtCourses[].price` | PT 강습 가격입니다. |
| `data.inProgressPtCourses[].currentStudentCount` | 해당 강습의 `IN_PROGRESS` 상태 수강생 수입니다. |

### 정렬·빈 데이터 정책

- 카드 목록은 `currentStudentCount DESC`, `ptCourseId DESC` 순서입니다.
- `BLOCKED`, `DELETED`, soft delete된 PT 강습은 집계와 카드 목록에서 제외합니다.
- 소속 조직이나 현재 수강생이 없으면 각각 `0`을 반환합니다.
- 수강생이 있는 강습을 먼저 반환하고, 남은 자리는 수강생이 `0`명인 내 활성 강습으로 채웁니다.
- 내 활성 강습이 4개보다 적으면 있는 만큼만 반환합니다.

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `AUTH_401_001` | Access Token이 없습니다. | Authorization Header가 없을 때 |
| `401 Unauthorized` | `AUTH_401_002` | Access Token이 만료되었습니다. | Access Token이 만료됐을 때 |
| `401 Unauthorized` | `AUTH_401_003` | 유효하지 않은 Access Token입니다. | 토큰 검증에 실패했을 때 |
| `403 Forbidden` | `AUTH_403_001` | 접근 권한이 없습니다. | `TRAINER` 권한이 아닌 계정으로 접근했을 때 |
| `404 Not Found` | `TRAINER_PROFILE_404_1` | 트레이너 프로필을 찾을 수 없습니다. | 로그인 사용자의 활성 트레이너 프로필이 없을 때 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

---

## 2. 공개 트레이너 프로필 상세 조회

`GET /api/trainers/{trainerProfileId}`  
권한: 없음 · 공개 API

# **[request]**

Request Header

없음

Path Variable

| name | type | 필수 | 설명 |
| --- | --- | --- | --- |
| `trainerProfileId` | `Long` | 필수 | 조회할 트레이너 프로필 ID. 1 이상의 양수여야 합니다. |

Request Parameter / Body

없음

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 공개 트레이너 프로필 상세 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "TRAINER_PROFILE_200_3",
  "message": "트레이너 프로필 상세 조회에 성공했습니다.",
  "data": {
    "trainerProfileId": 7,
    "profileImageUrl": "https://example.com/profile.jpg",
    "trainerName": "홍길동",
    "introduction": "체형 교정 PT를 전문으로 합니다.",
    "averageRating": 4.9,
    "reviewCount": 43,
    "status": "ACTIVE",
    "certifications": [{
      "trainerCertificationId": 11,
      "name": "생활스포츠지도사 2급",
      "certificationType": "REQUIRED"
    }],
    "awards": [{
      "trainerAwardId": 5,
      "name": "2025 피트니스 대회 입상"
    }]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.trainerProfileId` | 트레이너 프로필 ID입니다. |
| `data.profileImageUrl` | 공개 프로필 이미지 URL입니다. 이미지가 없거나 URL 조회에 실패하면 `null`입니다. |
| `data.trainerName` | 트레이너명입니다. |
| `data.introduction` | 트레이너 자기소개입니다. |
| `data.averageRating` | 평균 강사 평점입니다. |
| `data.reviewCount` | 강사평 수입니다. |
| `data.status` | 트레이너 프로필 상태입니다. |
| `data.certifications` | 자격증 목록입니다. 파일 URL은 공개하지 않습니다. |
| `data.certifications[].trainerCertificationId` | 자격증 ID입니다. |
| `data.certifications[].name` | 자격증명입니다. |
| `data.certifications[].certificationType` | 자격증 유형입니다. `REQUIRED`, `ADDITIONAL` 중 하나입니다. |
| `data.awards` | 수상·대회 경력 목록입니다. 값이 없으면 빈 배열 `[]`입니다. |
| `data.awards[].trainerAwardId` | 수상 경력 ID입니다. |
| `data.awards[].name` | 수상·대회 경력명입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | `trainerProfileId`가 0 이하인 경우 |
| `404 Not Found` | `TRAINER_PROFILE_404_1` | 트레이너 프로필을 찾을 수 없습니다. | 존재하지 않는 프로필 ID인 경우 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

---

## 3. 내 트레이너 프로필 상세 조회

`GET /api/trainers/me`  
권한: `TRAINER`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` · `TRAINER` 권한의 Access Token |

Request Parameter / Body

없음

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 내 트레이너 프로필 상세 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "TRAINER_PROFILE_200_1",
  "message": "내 트레이너 프로필 상세 조회에 성공했습니다.",
  "data": {
    "trainerProfileId": 7,
    "profileImageUrl": "https://example.com/profile.jpg",
    "profileImageOriginalName": "profile.jpg",
    "trainerName": "홍길동",
    "introduction": "체형 교정 PT를 전문으로 합니다.",
    "averageRating": 4.9,
    "reviewCount": 43,
    "status": "ACTIVE",
    "certifications": [{
      "trainerCertificationId": 11,
      "name": "생활스포츠지도사 2급",
      "certificationType": "REQUIRED",
      "fileUrl": "https://example.com/certification.pdf",
      "fileOriginalName": "certificate.pdf"
    }],
    "awards": []
  }
}
```

### Response Field

공개 상세 조회의 공통 필드에 더해, 본인 조회에서는 아래 파일 정보를 반환합니다.

| name | 설명 |
| --- | --- |
| `data.profileImageOriginalName` | 프로필 이미지 원본 파일명입니다. 이미지가 없으면 `null`입니다. |
| `data.certifications[].fileUrl` | 자격증 파일 URL입니다. 파일이 없는 추가 자격증은 `null`입니다. |
| `data.certifications[].fileOriginalName` | 자격증 파일 원본명입니다. 파일이 없으면 `null`입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `AUTH_401_001` | Access Token이 없습니다. | 인증 정보가 없을 때 |
| `403 Forbidden` | `AUTH_403_001` | 접근 권한이 없습니다. | `TRAINER` 권한이 아닐 때 |
| `404 Not Found` | `TRAINER_PROFILE_404_1` | 트레이너 프로필을 찾을 수 없습니다. | 로그인한 사용자의 프로필이 없을 때 |

---

## 4. 내 트레이너 프로필 수정

`PATCH /api/trainers/me`  
권한: `TRAINER`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` · `TRAINER` 권한의 Access Token |
| `Content-Type` | `application/json` |

Request Parameter

없음

Request Body

```json
{
  "profileImageAction": "REPLACE",
  "profileImageFile": {
    "fileKey": "uploads/profiles/trainers/7/profile.jpg",
    "originalName": "profile.jpg",
    "contentType": "image/jpeg",
    "fileSize": 314288
  },
  "additionalCertifications": ["NSCA-CPT"],
  "awardHistories": ["2025 피트니스 대회 입상"],
  "introduction": "수정된 트레이너 자기소개입니다."
}
```

### Request Body Field

| name | 설명 |
| --- | --- |
| `profileImageAction` | 프로필 이미지 수정 방식입니다. `KEEP`, `REPLACE`, `DELETE` 중 하나이며 필수입니다. |
| `profileImageFile` | 새 이미지 파일 메타데이터입니다. `REPLACE`일 때 필수이고, `KEEP`, `DELETE`일 때는 `null`이어야 합니다. |
| `profileImageFile.fileKey` | 업로드된 파일 Key입니다. |
| `profileImageFile.originalName` | 원본 파일명입니다. |
| `profileImageFile.contentType` | 파일 Content-Type입니다. |
| `profileImageFile.fileSize` | 파일 크기입니다. byte 단위입니다. |
| `additionalCertifications` | 추가 자격증 전체 목록입니다. `null`이면 유지, 빈 배열이면 모든 추가 자격증을 삭제합니다. 최대 30개이며 항목당 최대 100자입니다. |
| `awardHistories` | 수상·대회 경력 전체 목록입니다. `null`이면 유지, 빈 배열이면 모든 수상 경력을 삭제합니다. 최대 100개이며 항목당 최대 150자입니다. |
| `introduction` | 자기소개입니다. `null`이면 유지하고, 빈 문자열이면 빈 값으로 수정합니다. 최대 1000자입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `201 Created` | 내 트레이너 프로필 수정 성공 |

Response Body

```json
{
  "status": 201,
  "code": "TRAINER_PROFILE_201_2",
  "message": "트레이너 프로필 수정이 완료되었습니다.",
  "data": {
    "trainerProfileId": 7
  }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `TRAINER_PROFILE_400_1` | 트레이너 프로필 수정 요청값이 유효하지 않습니다. | 이미지 수정 규칙이 맞지 않거나 수정 Command가 유효하지 않은 경우 |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | 목록 크기, 항목 공백, 문자열 길이 등 Bean Validation에 실패한 경우 |
| `401 Unauthorized` | `AUTH_401_001` | Access Token이 없습니다. | 인증 정보가 없을 때 |
| `403 Forbidden` | `AUTH_403_001` | 접근 권한이 없습니다. | `TRAINER` 권한이 아닐 때 |
| `404 Not Found` | `TRAINER_PROFILE_404_1` | 트레이너 프로필을 찾을 수 없습니다. | 로그인한 사용자의 프로필이 없을 때 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 파일 등록 또는 예상하지 못한 서버 오류 |

---

## 5. 트레이너 검색

`GET /api/trainers/search?keyword={keyword}&page={page}&size={size}`  
권한: `ORGANIZATION`, `ADMIN`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` · `ORGANIZATION` 또는 `ADMIN` 권한의 Access Token |

Request Parameter

| name | type | 필수 | 설명 |
| --- | --- | --- | --- |
| `keyword` | `String` | 선택 | 이름, username, nickname의 접두어 검색어입니다. `null` 또는 공백이면 전체 ACTIVE 트레이너를 조회합니다. 최대 100자입니다. |
| `page` | `Integer` | 선택 | 0부터 시작하는 페이지 번호입니다. 기본값은 `0`입니다. |
| `size` | `Integer` | 선택 | 페이지 크기입니다. 기본값은 `10`, 허용 범위는 1~100입니다. |

Request Body

없음

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 트레이너 검색 성공 |

Response Body

```json
{
  "status": 200,
  "code": "TRAINER_PROFILE_200_4",
  "message": "트레이너 검색에 성공했습니다.",
  "data": {
    "content": [{
      "trainerProfileId": 7,
      "name": "홍길동",
      "username": "trainer01@test.com",
      "nickname": "운동왕"
    }],
    "page": 0,
    "size": 10,
    "hasNext": false
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.content` | 검색된 ACTIVE 트레이너 목록입니다. 결과가 없으면 빈 배열 `[]`입니다. |
| `data.content[].trainerProfileId` | 트레이너 프로필 ID입니다. |
| `data.content[].name` | 사용자 실명입니다. |
| `data.content[].username` | 사용자 로그인 아이디입니다. |
| `data.content[].nickname` | 사용자 닉네임입니다. |
| `data.page` | 현재 페이지 번호입니다. |
| `data.size` | 현재 페이지 크기입니다. |
| `data.hasNext` | 다음 페이지 존재 여부입니다. |

### 조회 정책

- 검색어는 앞부분 일치(`prefix`) 기준으로 username, name, nickname을 검색합니다.
- 결과는 `name ASC`, `trainerProfileId ASC` 순서입니다.
- `Slice` 기반 조회이므로 `totalElements`, `totalPages`는 반환하지 않습니다.

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | page·size 범위 또는 keyword 길이가 유효하지 않은 경우 |
| `401 Unauthorized` | `AUTH_401_001` | Access Token이 없습니다. | 인증 정보가 없을 때 |
| `403 Forbidden` | `AUTH_403_001` | 접근 권한이 없습니다. | `ORGANIZATION`, `ADMIN` 권한이 아닐 때 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

## 📝 문서 정보

- 업데이트일: `2026-07-21`
- 변경 사항(요약):
  - 트레이너의 조직·수강생·진행 강습·만족도를 한 번에 조회하는 대시보드 API를 추가했습니다. 🏋️
  - 수강생 집계와 카드 정렬·빈 값 정책을 명세화했습니다. 📊
  - 공개·내 프로필 조회, 프로필 수정, 조직·관리자 검색 API 명세를 추가했습니다. 📚
  - 수강생이 없는 내 활성 PT 강습도 카드 목록에 포함하도록 정책을 확장했습니다. 🪪
  - 소속 조직 ID가 없는 강습 카드의 `organizationName: null` 처리 정책을 명시했습니다. 🛡️
