# 🔄 OCR 도메인 Flow

## 1. 책임과 호출 경계

```text
TrainerApplication 도메인
→ 자격증 파일 등록·다운로드·신청 정책 검증을 담당

OCR 도메인
→ 파일 바이트를 Clova OCR에 전달하고 표준 OCR 결과로 변환

Naver Clova OCR
→ 이미지/PDF 분석, 템플릿 매칭, 필드 추출 수행
```

- OCR 도메인은 파일 ID, S3 경로, 트레이너 신청 저장을 알지 못합니다.
- OCR 도메인은 `originalFilename`, `contentType`, `fileBytes`만 받습니다.
- 필수 자격증의 인정 여부는 OCR 도메인이 아니라 `TrainerApplication` 도메인이 결정합니다.

## 2. 트레이너 신청에서의 전체 흐름

```text
USER Access Token
→ POST /api/trainer-applications
→ TrainerApplicationController
→ TrainerApplicationCommandService.create(...)
→ FileUseCase로 certificateFile 등록
→ FileUseCase로 등록된 certificateFile 내용 다운로드
→ FileContentResult(originalName, contentType, bytes)
→ ExtractOcrCommand 변환
→ OcrUseCase.extractOcr(command)
→ OcrService
→ OcrClientPort
→ ClovaOcrClientAdapter
→ Naver Clova OCR
→ OcrResult 반환
→ TrainerApplicationCommandService.validateRequiredCertification(...)
→ 자격증 검증 성공 시 트레이너 신청서 저장
```

OCR 또는 자격증 검증이 실패하면 신청서 저장 전에 예외가 발생합니다. 이때 이미 등록한 파일은 `TrainerApplicationCommandService`가 안전하게 정리합니다.

## 3. OCR 도메인 내부 흐름

```text
OcrUseCase.extractOcr(command)
→ OcrService.validateCommand(command)
→ fileBytes null·빈 배열 검사
→ OcrMetric으로 전체 처리 시간 측정 시작
→ OcrClientPort.extractOcr(command)
→ ClovaOcrClientAdapter.extractOcr(command)
→ MIME Type 우선, 파일 확장자 보조로 jpg/png/pdf format 결정
→ multipart message JSON 생성
→ multipart file 파트 생성
→ RestClient POST 요청
→ ClovaOcrResponse 수신
→ images[0]·inferResult·fields 검증
→ Clova DTO를 OcrResult로 변환
→ OcrMetric으로 성공·실패·처리 시간 기록
```

## 4. Clova 요청·응답 변환 흐름

```text
ExtractOcrCommand
→ originalFilename, contentType, fileBytes
→ resolveFormat(...)
→ jpg | png | pdf
→ ClovaOcrMessageRequest.of(requestId, format)
→ version=V2, timestamp, images[0].format/name 생성
→ MultipartBodyBuilder
→ message: application/json
→ file: 원본 MIME Type의 실제 파일 바이트
→ X-OCR-SECRET Header 추가
→ clova.ocr.invoke-url로 POST

ClovaOcrResponse
→ images[0] 선택
→ inferResult == SUCCESS 확인
→ matchedTemplate.name → OcrResult.matchedTemplateName
→ fields[].name/inferText/inferConfidence → OcrExtractedField
```

## 5. 재시도·타임아웃 정책

```text
Clova 5xx 응답
또는 ResourceAccessException(네트워크·타임아웃)
→ ClovaOcrRetryableException
→ Spring Retry 재시도
→ 최대 3회 시도
→ 500ms 대기 후 재시도
→ 이후 대기 시간은 2배씩 증가
→ 모두 실패하면 @Recover
→ OCR_502_1 반환
```

- 연결 시간 제한은 3초입니다.
- 읽기 시간 제한은 30초입니다.
- Clova 4xx, 메시지 JSON 직렬화 오류, 기타 RestClient 오류는 재시도하지 않습니다.
- 재시도 소진 시 `gymjjak.ocr.retry.exhausted.total` 카운터를 증가시킵니다.

## 6. 트레이너 신청 자격증 검증 정책

```text
OcrResult
→ matchedTemplateName이 생활스포츠지도사인지 확인
→ 자격등급 필드가 공백이 아닌지 확인
→ 자격종목 필드가 공백이 아닌지 확인
→ 자격취득일 필드가 공백이 아닌지 확인
→ 모두 충족
→ 트레이너 신청 저장

하나라도 불충족
→ RequiredCertificationNotVerifiedException
→ TRAINER_APPLICATION_400_2 반환
```

## 7. 관측 지점

| 메트릭 | 주요 태그 | 의미 |
| --- | --- | --- |
| `gymjjak.ocr.extract.duration` | `content_type`, `outcome` | OCR UseCase 전체 처리 시간 |
| `gymjjak.ocr.external.request.duration` | `provider`, `format`, `outcome`, `reason` | Clova 외부 API 요청 처리 시간과 실패 원인 |
| `gymjjak.ocr.retry.exhausted.total` | `provider` | 재시도 3회가 모두 소진된 횟수 |

로그에는 내부 `requestId`, Clova `requestId`, 파일 크기, 파일 형식, 처리 시간이 기록됩니다. 파일 바이트와 Clova Secret Key는 로그로 남기지 않습니다. 🔒

## 📝 문서 정보

- 업데이트일: `2026-07-21`
- 변경 사항(요약):
  - 트레이너 신청 → File → OCR → Clova → 자격증 정책 검증 흐름을 정리했습니다. 🔄
  - 형식 판별, multipart 요청, 재시도·타임아웃·메트릭 정책을 코드 기준으로 기록했습니다. ⚡
  - OCR 추출과 트레이너 신청 정책 검증의 책임 경계를 명확히 했습니다. 🧩
