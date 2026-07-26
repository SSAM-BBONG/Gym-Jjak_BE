# 🔎 OCR 도메인 API·연동 명세

## 1. 공개 REST API 여부

OCR 도메인은 별도 Controller를 제공하지 않습니다. 따라서 `/api/ocr` 형태의 직접 호출 API는 없으며, 현재는 트레이너 신청 도메인이 `OcrUseCase`를 내부 호출합니다.

| 구분 | 현재 구현 |
| --- | --- |
| 외부 사용자 호출 | 없음 |
| 내부 호출자 | `TrainerApplicationCommandService` |
| 실제 사용자 API | `POST /api/trainer-applications` |
| OCR Provider | Naver Clova OCR |

## 2. 내부 UseCase 계약

### 요청

`OcrUseCase.extractOcr(ExtractOcrCommand command)`

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `originalFilename` | `String` | 선택 | 원본 파일명입니다. Content-Type으로 형식을 판단할 수 없을 때 확장자를 보조 수단으로 사용합니다. |
| `contentType` | `String` | 선택 | 파일 MIME Type입니다. `image/jpeg`, `image/png`, `application/pdf`를 지원합니다. |
| `fileBytes` | `byte[]` | 필수 | OCR에 전달할 실제 파일 바이트입니다. `null` 또는 빈 배열이면 실패합니다. |

### 성공 반환값

`OcrResult`

| 필드 | 타입 | null 허용 | 설명 |
| --- | --- | --- | --- |
| `matchedTemplateName` | `String` | 허용 | Clova가 매칭한 템플릿명입니다. 템플릿을 찾지 못하면 `null`일 수 있습니다. |
| `fields` | `List<OcrExtractedField>` | 비허용 | 추출 필드 목록입니다. Clova가 빈 배열을 반환하면 빈 목록 `[]`으로 유지합니다. |
| `fields[].name` | `String` | 허용 | Clova 템플릿의 필드명입니다. |
| `fields[].inferText` | `String` | 허용 | OCR로 인식한 텍스트입니다. |
| `fields[].inferConfidence` | `Double` | 허용 | OCR 인식 신뢰도입니다. |

## 3. 실제 사용자 API에서의 사용

현재 OCR은 트레이너 신청 생성 시 필수 자격증을 검증하는 용도로만 연결되어 있습니다.

`POST /api/trainer-applications`

- 신청 도메인이 File 도메인에서 등록한 `certificateFile`을 다운로드합니다.
- 다운로드한 파일명·MIME Type·바이트를 `ExtractOcrCommand`로 변환해 OCR 도메인에 전달합니다.
- OCR 결과의 템플릿명이 `생활스포츠지도사`인지 확인합니다.
- `자격등급`, `자격종목`, `자격취득일` 필드가 모두 공백이 아닌지 확인합니다.
- 검증에 성공한 경우에만 트레이너 신청서를 저장합니다.

> Clova가 `NOT_FOUND: not found matched template` 메시지 또는 `validationResult.result = NO_REQUESTED`를 반환하면, Adapter는 템플릿 미매칭으로 판단해 `matchedTemplateName = null`, `fields = []`인 OCR 결과를 반환합니다. 이는 외부 OCR 장애가 아니므로 재시도하지 않으며, 트레이너 신청의 필수 자격증 정책에서 `TRAINER_APPLICATION_400_2`로 실패합니다.

> OCR의 `fields`가 빈 배열인 것은 OCR 도메인 자체에서는 허용되는 결과입니다. 다만 트레이너 신청의 필수 자격증 정책에서는 요구 필드가 없으므로 `TRAINER_APPLICATION_400_2`로 실패합니다.

## 4. Naver Clova OCR 요청 계약

OCR Adapter는 Clova Invoke URL에 `multipart/form-data` 요청을 전송합니다.

| 구분 | 값 |
| --- | --- |
| HTTP Method | `POST` |
| URL | `clova.ocr.invoke-url` 설정값 |
| 인증 Header | `X-OCR-SECRET: {secretKey}` |
| Part `message` | JSON 문자열. `version: V2`, 내부 생성 `requestId`, 요청 시각, 파일 format을 포함합니다. |
| Part `file` | 실제 파일 바이트입니다. 파일명은 원본 파일명이며 없으면 `ocr-image`를 사용합니다. |
| 지원 format | `jpg`, `png`, `pdf` |

Clova 응답에서 첫 번째 `images[0]`만 사용합니다. `inferResult`가 `SUCCESS`가 아니더라도 `NOT_FOUND: not found matched template` 또는 `validationResult.result = NO_REQUESTED`이면 템플릿 미매칭 결과로 처리합니다. 그 밖의 `inferResult != SUCCESS`, `images` 누락·빈 배열, `fields == null`은 유효하지 않은 OCR 응답으로 처리합니다.

## 5. 오류 코드

OCR 도메인의 예외는 별도 Controller가 없으므로, 호출한 API의 전역 예외 응답으로 반환됩니다.

| HTTP 상태 | code | message | 발생 조건 |
| --- | --- | --- | --- |
| `400 Bad Request` | `OCR_400_1` | OCR 파일을 읽을 수 없습니다. | `ExtractOcrCommand`가 `null`이거나 파일 바이트가 `null`·빈 배열인 경우 |
| `400 Bad Request` | `OCR_400_2` | 지원하지 않는 OCR 파일 형식입니다. | MIME Type과 파일 확장자로 `jpg`, `png`, `pdf`를 판별할 수 없는 경우 |
| `502 Bad Gateway` | `OCR_502_1` | OCR 요청에 실패했습니다. | Clova 4xx, 직렬화 실패, 기타 RestClient 오류 또는 재시도 소진 |
| `502 Bad Gateway` | `OCR_502_2` | OCR 응답 형식이 올바르지 않습니다. | `images` 누락·빈 배열, 템플릿 미매칭이 아닌 `inferResult != SUCCESS`, `fields == null`인 경우 |
| `400 Bad Request` | `TRAINER_APPLICATION_400_2` | 필수 자격증을 확인할 수 없습니다. 올바른 자격증 이미지를 업로드해 주세요. | Clova 템플릿 미매칭 또는 트레이너 신청의 템플릿·필수 필드 정책을 통과하지 못한 경우 |

## 📝 문서 정보

- 업데이트일: `2026-07-22`
- 변경 사항(요약):
  - OCR 도메인이 공개 REST API가 아닌 내부 UseCase 연동 구조임을 명시했습니다. 🔗
  - Clova OCR multipart 요청·응답 변환과 트레이너 신청 자격증 검증 정책을 정리했습니다. 🪪
  - OCR 인프라 오류와 신청 도메인 정책 오류를 구분해 기록했습니다. 🧭
  - Clova 템플릿 미매칭을 `400 / TRAINER_APPLICATION_400_2`로 처리하는 기준과 재시도 제외 정책을 반영했습니다. 🪪
