# 📁 파일 API Flow

> 이 문서는 파일 도메인의 내부 흐름을 설명합니다.

---

## 1. 책임 범위

| 구성요소 | 책임 |
| --- | --- |
| `FileController` | Presigned URL 발급 및 조회 URL 발급 |
| `FileService` | Presigned URL 생성, 파일 등록, 조회 URL 발급, 다운로드, 삭제 |
| `FileUrlService` | 타 도메인에서 fileId로 URL을 조회할 때 사용하는 내부 UseCase |
| `FilePolicy` | fileType별 허용 MIME, 최대 크기, 소유권 체크 여부 정의 |
| `FileStoragePort` | S3 Presigned URL 생성·공개 URL 반환·다운로드·삭제 경계 |
| `FileRepository` | 파일 메타데이터 저장·조회 |

---

## 2. Presigned PUT URL 발급 (`POST /api/files/presigned-urls`)

```text
클라이언트
  → FileController
  → commands 목록 생성 (uploaderId, fileType, contentType)
  → FileService.generatePresignedUploadUrls(commands)
  → 각 command에 대해:
      → FilePolicy.from(fileType)
      → policy.isAllowed(contentType)       ← 허용 MIME 아니면 FILE_400_2
      → key = "{fileType.path}/{uploaderId}/{UUID}"
      → FileStoragePort.generatePresignedUploadUrl(key, contentType)
      → 메트릭 기록
      → PresignedUrlResult(presignedUrl, fileKey) 반환
  → 200 응답 (files 목록)
```

### 단계별 설명

1. `FilePolicy`가 `fileType`별 허용 MIME 타입을 검증합니다. 허용되지 않으면 `FILE_400_2`를 반환합니다.
2. S3 오브젝트 키는 `{fileType경로}/{uploaderId}/{UUID}` 형식으로 생성됩니다. 업로더 ID가 경로에 포함되어 소유권 추적이 가능합니다.
3. Presigned URL은 `FileStoragePort`를 통해 S3 SDK로 생성됩니다. 실제 파일은 이 시점에 업로드되지 않습니다.
4. 클라이언트는 반환된 `presignedUrl`로 S3에 직접 PUT 업로드합니다.
5. 업로드 후 `fileKey`를 트레이너 신청·조직 신청 등 도메인 API에 전달하면, 해당 도메인에서 `FileRegistrationPort`를 통해 파일을 등록합니다.

---

## 3. 파일 등록 (내부 흐름 — REST 직접 노출 없음)

```text
타 도메인 서비스 (트레이너 신청, 조직 신청 등)
  → FileRegistrationPort.register(fileKey, originalName, contentType, fileSize, fileType)
  → FileService.registerFiles(commands)
  → 각 command에 대해:
      → fileKey 접두사 검증: "{fileType.path}/{uploaderId}/" 로 시작해야 함
          └─ 불일치 시 FILE_403_1 (타인의 S3 경로 조작 방어)
      → File.create(uploaderId, originalName, storedName, fileKey, contentType, fileSize, fileType)
      → FileRepository.save()
      → 메트릭 기록
      → FileRegistrationResult(fileId, fileType) 반환
```

### 단계별 설명

1. REST API로 직접 노출되지 않고, 트레이너 신청·조직 신청 등 도메인 서비스가 내부적으로 호출합니다.
2. `fileKey` 접두사 검증으로 다른 사용자가 생성한 S3 오브젝트 키를 제출하는 것을 방어합니다.
3. 등록 결과로 반환된 `fileId`를 해당 도메인 엔티티(트레이너 신청, 조직 신청 등)에 저장합니다.

---

## 4. 파일 조회 URL 발급 (`GET /api/files/{fileId}/presigned-url`)

```text
클라이언트
  → FileController
  → FileService.getPresignedUrl(GetPresignedUrlCommand(fileId, requesterId, isAdmin))
  → FileRepository.findById(fileId)
      └─ 없으면 FILE_404_1
  → FilePolicy.from(file.fileType)
  → policy.isRequiresOwnershipCheck() 확인
      ├─ false (공개 파일: PROFILE_IMAGE, PT_THUMBNAIL)
      │     → FileStoragePort.getPublicUrl(fileUrl)
      │     → 공개 URL 반환 (만료 없음)
      └─ true (비공개 파일: CERTIFICATION, AWARD, BUSINESS_LICENSE, FEEDBACK_VIDEO, MEAL_IMAGE)
            → !isAdmin AND uploaderId != requesterId → FILE_403_1
            → FileStoragePort.getPresignedUrl(fileUrl)
            → Presigned GET URL 반환 (만료 시간 있음)
  → 200 응답 (url)
```

### 파일 접근 정책

| fileType | 접근 제어 | URL 타입 |
| --- | --- | --- |
| `PROFILE_IMAGE` | 모든 인증 사용자 | 공개 URL |
| `PT_THUMBNAIL` | 모든 인증 사용자 | 공개 URL |
| `CERTIFICATION` | 소유자 또는 ADMIN | Presigned GET URL |
| `AWARD` | 소유자 또는 ADMIN | Presigned GET URL |
| `BUSINESS_LICENSE` | 소유자 또는 ADMIN | Presigned GET URL |
| `FEEDBACK_VIDEO` | 소유자 또는 ADMIN | Presigned GET URL |
| `MEAL_IMAGE` | 소유자 또는 ADMIN | Presigned GET URL |

---

## 5. FileUrlService — 타 도메인 내부 조회 흐름

`FileUrlService`는 타 도메인(조직 상세 조회, 채팅 목록 등)에서 fileId로 URL을 직접 조회할 때 사용합니다. REST 컨트롤러가 아닌 서비스 레이어 간 내부 호출입니다.

```text
타 도메인 서비스
  → FileUrlUseCase.getUrl(fileId, requesterId, isAdmin)
  → FileRepository.findById(fileId)
  → FilePolicy 확인
      ├─ 공개 → FileStoragePort.getPublicUrl()
      └─ 비공개 → FileUseCase.getPresignedUrl() (소유권 체크 포함)
  → FileUrlResult(url, originalName) 반환

  또는

  → FileUrlUseCase.getUrls(fileIds, requesterId, isAdmin)
  → FileRepository.findAllByIds(fileIds)    ← 일괄 조회
  → 각 파일별 정책 판단 후 URL 생성
  → Map<fileId, FileUrlResult> 반환         ← N+1 방지
```

### 단계별 설명

1. `getUrl()`: 단건 조회. 조직 신청 상세, 내 조직 정보 조회 등에서 사용합니다.
2. `getUrls()`: 일괄 조회. 채팅 목록에서 트레이너 프로필 이미지를 한 번에 조회할 때 등에서 사용합니다.
3. 비공개 파일의 경우 소유권 체크를 `FileUseCase.getPresignedUrl()`에 위임하므로, 호출부에서 `requesterId`와 `isAdmin`을 정확하게 전달해야 합니다.

---

## 6. FilePolicy — fileType별 정책 정의

```text
FilePolicy.from(FileType)
  → fileType에 해당하는 정책 enum 반환
  → isAllowed(contentType)        허용 MIME 검증
  → isAllowedSize(fileSize)       최대 파일 크기 검증 (등록 시 사용)
  → isRequiresOwnershipCheck()    소유권 체크 필요 여부
```

`FileType`(global 도메인)은 S3 경로만 관리하고, `FilePolicy`(file 도메인)는 업로드 정책(허용 MIME, 최대 크기, 접근 제어)을 관리합니다. 두 개념이 분리되어 있으므로, 새로운 파일 타입 추가 시 `FileType`과 `FilePolicy` 모두 수정해야 합니다.

---

## 7. 타 도메인 개발자 체크포인트 ✅

1. 파일 업로드 흐름은 항상 **Presigned URL 발급 → S3 직접 PUT → fileKey 전달 → 도메인 API에서 등록** 순서입니다.
2. 파일 등록(`registerFiles`)은 `FileRegistrationPort`를 통해 호출합니다. 직접 `FileService`를 의존하지 않습니다.
3. 파일 URL 조회는 `FileUrlUseCase`를 사용합니다. 다건은 `getUrls()`로 일괄 처리하여 N+1을 방지합니다.
4. 비공개 파일(`CERTIFICATION` 등)의 URL을 클라이언트에 전달할 때 Presigned URL의 만료 시간을 고려해야 합니다.
5. 새 fileType 추가 시 `FileType` enum(global)과 `FilePolicy` enum(file) 두 곳을 모두 수정해야 합니다.

---

## 📝 문서 정보

- 작성일: `2026-07-21`
