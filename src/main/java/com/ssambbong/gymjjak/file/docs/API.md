# 📁 파일 API

> 작성일: 2026-07-21
> 대상: `FileController`
> 공통 응답 형식: `status`, `code`, `message`, `data`
> 기본 경로: `/api/files`

## 기능 개요

- S3 직접 업로드를 위한 Presigned PUT URL을 발급합니다.
- 업로드된 파일의 조회 URL(공개 URL 또는 Presigned GET URL)을 발급합니다.

### S3 직접 업로드 흐름

```
1. POST /api/files/presigned-urls 호출 → presignedUrl, fileKey 수신
2. presignedUrl로 S3에 직접 PUT 업로드 (백엔드 미경유)
3. 트레이너 신청·조직 신청 등 관련 API 호출 시 fileKey + 파일 메타데이터 함께 전달
```

---

## 1. Presigned URL 일괄 발급

`POST /api/files/presigned-urls`

> 인증된 사용자 누구나 호출 가능. 한 번에 최대 10개까지 발급합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` |

Request Body

```json
{
  "files": [
    {
      "fileType": "PROFILE_IMAGE",
      "contentType": "image/jpeg"
    },
    {
      "fileType": "CERTIFICATION",
      "contentType": "application/pdf"
    }
  ]
}
```

| name | 필수 | description |
| --- | --- | --- |
| `files` | O | 발급할 파일 목록 (1~10개) |
| `files[].fileType` | O | 파일 타입. 아래 FileType 표 참고 |
| `files[].contentType` | O | 업로드할 파일의 MIME 타입 |

#### FileType 목록

| fileType | 허용 MIME | 최대 크기 | 접근 제어 |
| --- | --- | --- | --- |
| `PROFILE_IMAGE` | image/jpeg, image/png, image/webp, application/pdf | 10 MB | 공개 |
| `PT_THUMBNAIL` | image/jpeg, image/png, image/webp, application/pdf | 10 MB | 공개 |
| `CERTIFICATION` | image/jpeg, image/png, image/webp, application/pdf | 10 MB | 소유자/ADMIN |
| `AWARD` | image/jpeg, image/png, image/webp, application/pdf | 10 MB | 소유자/ADMIN |
| `BUSINESS_LICENSE` | image/jpeg, image/png, image/webp, application/pdf | 10 MB | 소유자/ADMIN |
| `FEEDBACK_VIDEO` | video/mp4, video/quicktime | 50 MB | 소유자/ADMIN |
| `MEAL_IMAGE` | image/jpeg, image/png, image/webp | 10 MB | 소유자/ADMIN |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `FILE_200_1` | Presigned URL 발급 성공 |

Response Body

```json
{
  "status": 200,
  "code": "FILE_200_1",
  "message": "Presigned URL 발급 성공",
  "data": {
    "files": [
      {
        "presignedUrl": "https://s3.ap-northeast-2.amazonaws.com/bucket/uploads/profiles/trainers/1/uuid?...",
        "fileKey": "uploads/profiles/trainers/1/550e8400-e29b-41d4-a716-446655440000"
      }
    ]
  }
}
```

| name | 설명 |
| --- | --- |
| `data.files[].presignedUrl` | S3 PUT 업로드용 Presigned URL. 유효 시간 내 한 번만 사용 |
| `data.files[].fileKey` | S3 오브젝트 키. 이후 API 호출 시 파일 식별자로 사용 |

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `FILE_400_2` | 허용되지 않는 파일 형식입니다. | fileType에 허용되지 않는 contentType |
| `400 Bad Request` | `COMMON_400` | 잘못된 요청입니다. | 필수값 누락, files 비어있음, 10개 초과 |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |

---

## 2. 파일 조회 URL 발급

`GET /api/files/{fileId}/presigned-url`

> 파일 접근 정책에 따라 공개 URL 또는 Presigned GET URL을 반환합니다.

### **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {accessToken}` |

Path Variable

| name | description |
| --- | --- |
| `fileId` | 조회할 파일 ID |

### **[response]**

#### 성공 코드

| HTTP 상태 | code | message |
| --- | --- | --- |
| `200 OK` | `FILE_200_3` | 파일 조회 URL 발급 성공 |

Response Body

```json
{
  "status": 200,
  "code": "FILE_200_3",
  "message": "파일 조회 URL 발급 성공",
  "data": "https://s3.ap-northeast-2.amazonaws.com/bucket/..."
}
```

> 공개 파일(`PROFILE_IMAGE`, `PT_THUMBNAIL`)은 만료 없는 공개 URL을 반환합니다.
> 비공개 파일(`CERTIFICATION`, `AWARD`, `BUSINESS_LICENSE`, `FEEDBACK_VIDEO`, `MEAL_IMAGE`)은 만료 시간이 있는 Presigned GET URL을 반환합니다.

#### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401` | 인증이 필요합니다. | 토큰 없음 또는 유효하지 않음 |
| `403 Forbidden` | `FILE_403_1` | 파일에 대한 접근 권한이 없습니다. | 비공개 파일 - 소유자도 ADMIN도 아님 |
| `404 Not Found` | `FILE_404_1` | 파일을 찾을 수 없습니다. | 존재하지 않는 fileId |

---

## 📝 문서 정보

- 작성일: `2026-07-21`
