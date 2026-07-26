# 🚨 신고 그룹 관리 API

> 작성일: 2026-07-20  
> 대상: `ReportGroupController`  
> 공통 응답 형식: `status`, `code`, `message`, `data`  
> 권한: 모든 API는 `ADMIN` 권한이 필요합니다. 🔒

## 기능 개요

신고 그룹은 동일한 신고 대상(`targetType + targetId`)에 접수된 개별 신고를 묶어 관리하는 루트 Aggregate입니다.

- 📋 관리자는 대상 유형별 신고 그룹 목록을 페이지 단위로 조회합니다.
- 🔎 신고 그룹 상세에서 개별 신고 사유와 처리 상태를 확인합니다.
- 🖼️ 관리자는 신고 접수 시점의 대상 스냅샷을 모달용으로 조회합니다.
- ✅/❌ 개별 신고를 승인 또는 반려하면 신고 그룹의 검토 상태와 유효 신고 수가 다시 계산됩니다.
- 🚫 관리자는 특정 신고 그룹의 대상을 수동 블라인드 처리할 수 있습니다.

> ✅ 승인·반려 응답의 `reportId`는 개별 신고 ID를 반환합니다.

---

## 1. 신고 그룹 목록 조회

`GET /api/reportgroup/list?targetType=PT_COURSE&page=0&size=10`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 관리자 인증 토큰입니다. |

Request Parameter

| name | 필수 | description |
| --- | --- | --- |
| `targetType` | O | 신고 대상 유형입니다. `PT_COURSE`, `TRAINER_REVIEW`, `COMMENT`, `POST`, `FEEDBACK`, `CHAT` 중 하나입니다. |
| `page` | X | 조회할 페이지 번호입니다. 기본값은 `0`이며, 0 이상이어야 합니다. |
| `size` | X | 페이지 크기입니다. 기본값은 `10`이며, 1~100 사이여야 합니다. |

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
| `200 OK` | `REPORT_200_2` | 신고 목록 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "REPORT_200_2",
  "message": "신고 목록 조회에 성공했습니다.",
  "data": {
    "reports": [
      {
        "reportGroupId": 10,
        "reportNumber": "RPT-20260720-001",
        "targetType": "PT",
        "targetId": 101,
        "targetDisplayText": "체형 교정 PT 프로그램",
        "targetOwnerUsername": "trainer01",
        "reportedAt": "2026-07-20T10:30:00",
        "effectiveReportCount": 3,
        "status": "대기중",
        "navigationType": "PAGE"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.reports` | 현재 페이지의 신고 그룹 목록입니다. |
| `data.reports[].reportGroupId` | 신고 그룹 ID입니다. 상세 조회 및 수동 블라인드 처리 시 사용합니다. |
| `data.reports[].reportNumber` | 신고 그룹의 식별 번호입니다. |
| `data.reports[].targetType` | 신고 대상 유형의 표시값입니다. |
| `data.reports[].targetId` | 신고 대상의 ID입니다. |
| `data.reports[].targetDisplayText` | 관리자 화면에 표시할 신고 대상 제목 또는 내용입니다. |
| `data.reports[].targetOwnerUsername` | 신고 대상 작성자의 사용자명입니다. |
| `data.reports[].reportedAt` | 가장 최근 신고 접수 일시입니다. |
| `data.reports[].effectiveReportCount` | 승인 상태인 유효 신고 수입니다. |
| `data.reports[].status` | 신고 그룹 검토 상태의 표시값입니다. |
| `data.reports[].navigationType` | 상세 화면 이동 방식입니다. `PAGE` 또는 `MODAL`입니다. |
| `data.page` | 현재 페이지 번호입니다. |
| `data.size` | 페이지 크기입니다. |
| `data.totalElements` | 전체 신고 그룹 수입니다. |
| `data.totalPages` | 전체 페이지 수입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 입력값입니다. | `targetType`이 누락되었거나 enum 값이 아니거나, `page`/`size` 범위를 벗어난 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | 관리자 권한이 없는 경우 |

---

## 2. 신고 그룹 상세 조회

`GET /api/reportgroup/detail/{reportGroupId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 관리자 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reportGroupId` | 조회할 신고 그룹 ID입니다. |

Request Body

```json

```

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `REPORT_200_3` | 신고 그룹 상세 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "REPORT_200_3",
  "message": "신고 상세 조회에 성공했습니다.",
  "data": {
    "reportGroupId": 10,
    "status": "대기중",
    "reports": [
      {
        "reportId": 1001,
        "reporterUsername": "user01",
        "reason": "욕설",
        "detail": "반복적인 욕설을 사용했습니다.",
        "reportedAt": "2026-07-20T10:30:00",
        "status": "대기"
      }
    ]
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.reportGroupId` | 신고 그룹 ID입니다. |
| `data.status` | 신고 그룹 검토 상태의 표시값입니다. `대기중`, `처리완료`, `반려` 중 하나입니다. |
| `data.reports` | 신고 그룹에 속한 개별 신고 목록입니다. |
| `data.reports[].reportId` | 개별 신고 ID입니다. 승인/반려 처리 시 사용합니다. |
| `data.reports[].reporterUsername` | 신고자의 사용자명입니다. |
| `data.reports[].reason` | 신고 사유의 표시값입니다. |
| `data.reports[].detail` | 신고자가 입력한 상세 사유입니다. |
| `data.reports[].reportedAt` | 신고 접수 일시입니다. |
| `data.reports[].status` | 개별 신고 처리 상태의 표시값입니다. `대기`, `승인`, `반려` 중 하나입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | 관리자 권한이 없는 경우 |
| `404 Not Found` | `REPORT_404_1` | 신고 그룹을 찾을 수 없습니다. | 존재하지 않거나 삭제된 신고 그룹인 경우 |

---

## 3. 개별 신고 승인

`PATCH /api/reportgroup/{reportGroupId}/reports/{reportId}/approve`

개별 신고를 승인하고, 신고 그룹의 검토 상태·유효 신고 수·자동 제재 상태를 재계산합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 관리자 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reportGroupId` | 개별 신고가 속한 신고 그룹 ID입니다. |
| `reportId` | 승인할 개별 신고 ID입니다. |

Request Body

```json

```

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `REPORT_200_5` | 신고 승인 처리 성공 |

Response Body

```json
{
  "status": 200,
  "code": "REPORT_200_5",
  "message": "신고 승인 처리에 성공했습니다.",
  "data": {
    "reportId": 1001,
    "reporterUsername": "user01",
    "reason": "욕설",
    "detail": "반복적인 욕설을 사용했습니다.",
    "reportedAt": "2026-07-20T10:30:00",
    "status": "승인"
  }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `REPORT_400_3` | 신고 그룹과 신고 정보가 일치하지 않습니다. | `reportId`가 `reportGroupId`에 속하지 않는 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | 관리자 권한이 없는 경우 |
| `404 Not Found` | `REPORT_404_1`, `REPORT_404_3` | 신고 그룹 또는 개별 신고를 찾을 수 없습니다. | 존재하지 않는 ID인 경우 |
| `409 Conflict` | `REPORT_409_1` | 이미 처리된 신고입니다. | 이미 승인 또는 반려 처리된 신고인 경우 |

---

## 4. 개별 신고 반려

`PATCH /api/reportgroup/{reportGroupId}/reports/{reportId}/reject`

개별 신고를 반려하고 유효 신고 수를 감소시킨 뒤, 신고 그룹의 검토·자동 제재 상태를 다시 계산합니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 관리자 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reportGroupId` | 개별 신고가 속한 신고 그룹 ID입니다. |
| `reportId` | 반려할 개별 신고 ID입니다. |

Request Body

```json

```

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `REPORT_200_6` | 신고 반려 처리 성공 |

Response Body

```json
{
  "status": 200,
  "code": "REPORT_200_6",
  "message": "신고 반려 처리에 성공했습니다.",
  "data": {
    "reportId": 1001,
    "reporterUsername": "user01",
    "reason": "욕설",
    "detail": "반복적인 욕설을 사용했습니다.",
    "reportedAt": "2026-07-20T10:30:00",
    "status": "반려"
  }
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `REPORT_400_3` | 신고 그룹과 신고 정보가 일치하지 않습니다. | `reportId`가 `reportGroupId`에 속하지 않는 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | 관리자 권한이 없는 경우 |
| `404 Not Found` | `REPORT_404_1`, `REPORT_404_3` | 신고 그룹 또는 개별 신고를 찾을 수 없습니다. | 존재하지 않는 ID인 경우 |
| `409 Conflict` | `REPORT_409_1` | 이미 처리된 신고입니다. | 이미 승인 또는 반려 처리된 신고인 경우 |

---

## 5. 신고 대상 수동 블라인드

`PATCH /api/reportgroup/{reportGroupId}/manual-blind`

관리자가 신고 그룹의 대상을 수동 블라인드 처리합니다. 처리 성공 시 대상 제재를 적용하고 해당 신고 그룹은 soft delete 됩니다.

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 관리자 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reportGroupId` | 수동 블라인드 처리할 신고 그룹 ID입니다. |

Request Body

```json

```

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `REPORT_200_9` | 신고 그룹 수동 블라인드 처리 성공 |

Response Body

```json
{
  "status": 200,
  "code": "REPORT_200_9",
  "message": "신고 그룹 수동 블라인드 처리가 성공했습니다.",
  "data": null
}
```

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | 관리자 권한이 없는 경우 |
| `404 Not Found` | `REPORT_404_1` | 신고 그룹을 찾을 수 없습니다. | 존재하지 않거나 삭제된 신고 그룹인 경우 |
| `409 Conflict` | `REPORT_409_4` | 신고 그룹 soft delete 처리에 실패했습니다. | 제재 적용 후 신고 그룹 soft delete에 실패한 경우 |

---

## 6. 신고 대상 스냅샷 조회

`GET /api/reportgroup/{reportGroupId}/snapshot`

관리자가 신고 접수 시점에 `ReportGroup`에 저장된 대상 스냅샷을 조회합니다. 원본 대상이 이후 수정되더라도, 이 API는 신고 당시 저장된 값을 반환합니다. 🖼️

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 관리자 인증 토큰입니다. |

Path Variable

| name | description |
| --- | --- |
| `reportGroupId` | 스냅샷을 조회할 신고 그룹 ID입니다. |

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
| `200 OK` | `REPORT_200_10` | 신고 스냅샷 조회 성공 |

Response Body

```json
{
  "status": 200,
  "code": "REPORT_200_10",
  "message": "신고 스냅샷 조회에 성공했습니다.",
  "data": {
    "reportGroupId": 10,
    "targetType": "댓글",
    "targetId": 301,
    "title": "댓글",
    "content": "신고된 댓글 내용",
    "fileUrl": null
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.reportGroupId` | 조회한 신고 그룹 ID입니다. |
| `data.targetType` | 신고 대상 유형의 화면 표시값입니다. 예: `PT`, `강사평`, `댓글`, `게시글`, `피드백`, `채팅` |
| `data.targetId` | 신고 대상 ID입니다. |
| `data.title` | 신고 접수 시점에 저장된 대상 제목입니다. 대상 유형에 따라 `null`일 수 있습니다. |
| `data.content` | 신고 접수 시점에 저장된 대상 본문 또는 메시지입니다. 대상 유형에 따라 `null`일 수 있습니다. |
| `data.fileUrl` | 신고 접수 시점에 저장된 첨부 파일 URL입니다. 첨부 파일이 없으면 `null`입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | ADMIN 권한이 아닌 경우 |
| `404 Not Found` | `REPORT_404_1` | 신고 그룹을 찾을 수 없습니다. | 존재하지 않거나 soft delete된 신고 그룹인 경우 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

---

## 🚨 신고 API

신고 대상의 타입과 ID, 신고 사유를 전달해 신고를 접수합니다. 인증된 `USER`, `TRAINER`, `ORGANIZATION`, `ADMIN` 권한이 사용할 수 있으며, 로그인한 사용자의 ID는 Access Token의 인증 정보에서 가져옵니다. 같은 대상에 대한 신고는 하나의 `ReportGroup`으로 관리되며, 신고 그룹이 처음 생성될 때 대상의 스냅샷 정보도 함께 저장됩니다.

### 신고 접수

`POST /api/reports`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` 형식의 인증 토큰입니다. `USER`, `TRAINER`, `ORGANIZATION`, `ADMIN` 권한이 필요합니다. |
| `Content-Type` | `application/json` |

Request Parameter

없음

Request Body

```json
{
  "targetId": 101,
  "targetType": "POST",
  "reason": "ADVERTISEMENT",
  "detail": "광고성 게시글로 보입니다."
}
```

| name | 설명 |
| --- | --- |
| `targetId` | 신고 대상의 ID입니다. 필수입니다. |
| `targetType` | 신고 대상 타입 enum입니다. 아래 `targetType` enum 목록 중 하나를 전달해야 합니다. |
| `reason` | 신고 사유 enum입니다. 아래 `reason` enum 목록 중 하나를 전달해야 합니다. |
| `detail` | 신고 상세 사유입니다. 필수이며 공백일 수 없고 최대 1,000자입니다. |

### `targetType` enum

| 값 | 설명 |
| --- | --- |
| `PT_COURSE` | PT 코스 |
| `TRAINER_REVIEW` | 강사평 |
| `COMMENT` | 댓글 |
| `POST` | 게시글 |
| `FEEDBACK` | 피드백 |
| `CHAT` | 채팅 |

### `reason` enum

| 값 | 설명 |
| --- | --- |
| `SPAM` | 도배 |
| `ADVERTISEMENT` | 광고 |
| `ABUSE` | 욕설 |
| `SEXUAL_CONTENT` | 음란물 |
| `FRAUD` | 사기 |
| `PRIVACY_EXPOSURE` | 개인정보 |
| `ETC` | 기타 |

> 로그인한 사용자의 ID는 Access Token에서 추출합니다. 요청 본문으로 신고자 ID를 전달하지 않습니다.

# **[response]**

### 성공코드

| HTTP 상태 | code | 설명 |
| --- | --- | --- |
| `200 OK` | `REPORT_200_1` | 신고가 성공적으로 접수됐습니다. |

Response Body

```json
{
  "status": 200,
  "code": "REPORT_200_1",
  "message": "신고가 성공적으로 접수됐습니다.",
  "data": null
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `status` | HTTP 응답 상태 코드입니다. |
| `code` | 신고 접수 성공 코드인 `REPORT_200_1`입니다. |
| `message` | `신고가 성공적으로 접수됐습니다.` |
| `data` | 신고 등록 결과로 프론트에 전달하는 데이터가 없으므로 `null`입니다. |

### 처리 정책

- 본인이 작성한 대상은 신고할 수 없습니다.
- 동일 사용자는 같은 신고 그룹에 중복 신고할 수 없습니다.
- 최초 신고이면 신고 그룹을 새로 만들고, 이후 신고이면 기존 그룹의 신고 수를 증가시킵니다.
- `PT_COURSE`, `POST`, `COMMENT`은 유효 신고 수가 5건 이상이면 자동 블라인드 처리 대상입니다.

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | `targetId`, `targetType`, `reason`, `detail` 누락 또는 `detail` 공백·1,000자 초과 |
| `400 Bad Request` | `REPORT_400_4` | 본인이 작성한 대상을 신고할 수 없습니다. | 대상 소유자와 신고자가 같은 경우 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | Access Token이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403` | 접근 권한이 없습니다. | 인증은 되었지만 허용된 권한이 아닌 경우 |
| `404 Not Found` | 대상 도메인별 오류 코드 | 신고 대상을 찾을 수 없습니다. | 대상 ID가 없거나 이미 삭제되어 스냅샷을 만들 수 없는 경우 |
| `409 Conflict` | `REPORT_409_3` | 동일한 대상에 중복 신고할 수 없습니다. | 같은 사용자가 같은 신고 그룹에 다시 신고한 경우 |
| `500 Internal Server Error` | `COMMON_500` | 서버 내부 오류가 발생했습니다. | 예상하지 못한 서버 오류 |

---

## 📝 문서 정보

- 업데이트일: `2026-07-23`
- 변경 사항(요약):
  - 신고 접수 요청의 `targetType`, `reason` enum 전체 값과 설명을 명시했습니다.
  - 신고 접수 API의 인증 권한, 요청 필드 제약, 성공 응답 필드를 보완했습니다.
  - 신고 접수 시점에 저장된 대상 스냅샷을 관리자 모달에서 조회하는 `GET /api/reportgroup/{reportGroupId}/snapshot` 명세를 추가했습니다. 🖼️
  - 승인·반려 응답의 `reportId`가 신고자 ID가 아닌 개별 신고 ID를 반환하도록 구현과 문서를 일치시켰습니다.
  - PT 코스 신고의 대상 소유자 ID를 트레이너 프로필 ID가 아닌 사용자 ID 기준으로 정정했습니다.
