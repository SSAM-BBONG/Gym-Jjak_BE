# 🛠️ OCR 도메인 Troubleshooting

## 1. 먼저 확인할 점

OCR 도메인은 직접 호출하는 REST API가 없습니다. 현재 오류는 대부분 `POST /api/trainer-applications` 요청 중 발생하며, 다음 순서로 확인합니다.

```text
트레이너 신청 요청 수신
→ certificateFile 등록 성공 여부
→ FileUseCase 다운로드 성공 여부
→ OcrService 입력값 검증
→ Clova 요청·응답·재시도 로그
→ 트레이너 신청의 템플릿·필수 필드 검증 결과
```

## 2. 증상별 점검

| 증상 | 코드상 원인 | 확인 방법 | 대응 |
| --- | --- | --- | --- |
| `OCR_400_1` | `fileBytes`가 `null` 또는 빈 배열입니다. | `event=trainer_application_certificate_ocr_started` 이전의 파일 다운로드 로그와 파일 크기를 확인합니다. | File 도메인의 다운로드 결과와 자격증 파일 등록 상태를 확인합니다. |
| `OCR_400_2` | MIME Type·파일 확장자로 `jpg`, `png`, `pdf`를 판별하지 못했습니다. | `event=ocr_request_started` 로그의 `contentType`, `format` 또는 예외 로그를 확인합니다. | 업로드 허용 파일 타입과 전달되는 MIME Type을 맞춥니다. |
| `OCR_502_1` | Clova 4xx, JSON 직렬화 실패, RestClient 오류 또는 재시도 소진입니다. | `event=ocr_request_failed`, `event=ocr_request_retryable_failed`, `event=ocr_request_retries_exhausted` 로그를 확인합니다. | 아래 3·4절 기준으로 HTTP 상태, 네트워크, 설정을 분리해 점검합니다. |
| `OCR_502_2` | Clova 응답에 `images`가 없거나 비었고, `inferResult`가 `SUCCESS`가 아니거나 `fields`가 `null`입니다. | `event=ocr_request_succeeded`가 없는지와 Clova 응답 계약 변경 여부를 확인합니다. | Clova 템플릿·응답 스키마·Invoke URL을 점검합니다. |
| `TRAINER_APPLICATION_400_2` | OCR 호출은 성공했지만 필수 자격증 정책이 실패했습니다. | `event=trainer_application_required_certification_not_verified` 로그의 templateName과 필드 존재 여부를 확인합니다. | `생활스포츠지도사` 템플릿 및 `자격등급`·`자격종목`·`자격취득일` 필드를 확인합니다. |

## 3. Clova 5xx·네트워크·타임아웃

```text
Clova 5xx 또는 ResourceAccessException
→ 재시도 대상
→ 최대 3회 시도
→ 모두 실패하면 OCR_502_1
```

확인 항목:

- `gymjjak.ocr.external.request.duration`에서 `reason=external_api_5xx`, `network_or_timeout` 여부를 확인합니다.
- `gymjjak.ocr.retry.exhausted.total{provider="clova"}` 증가 여부를 확인합니다.
- 애플리케이션의 Clova Invoke URL 접근 가능 여부와 DNS·방화벽·프록시 설정을 확인합니다.
- 연결 타임아웃은 3초, 읽기 타임아웃은 30초이므로 그보다 느린 외부 응답이 반복되는지 확인합니다.

## 4. 설정 문제

필수 설정은 다음 환경 변수로 주입됩니다. 실제 값은 문서·로그·소스에 기록하지 않습니다. 🔒

| 설정 키 | 환경 변수 | 역할 |
| --- | --- | --- |
| `clova.ocr.invoke-url` | `CLOVA_OCR_INVOKE_URL` | Clova OCR Invoke URL |
| `clova.ocr.secret-key` | `CLOVA_OCR_SECRET_KEY` | `X-OCR-SECRET` Header 값 |

- 둘 중 하나라도 비어 있으면 `ClovaOcrProperties` 생성 시 애플리케이션 시작이 실패합니다.
- 키 값 자체가 아니라 **환경 변수 존재 여부**, 배포 환경 주입 방식, URL 형식만 점검합니다.

## 5. 현재 코드상 유의 사항

### ⚠️ Clova 4xx도 현재는 `OCR_502_1`으로 반환

Clova가 4xx를 반환하면 Adapter는 재시도하지 않지만 `OCR_REQUEST_FAILED`로 변환합니다. 따라서 호출자에게는 외부 게이트웨이 오류인 `502`로 보이며, 파일 형식·템플릿·Secret Key 문제를 HTTP 상태만으로 구분할 수 없습니다.

```text
Clova 4xx
→ 재시도하지 않음
→ OCR_502_1 반환
→ 운영자는 로그의 statusCode·reason=external_api_4xx로 원인 확인 필요
```

향후 Clova의 4xx 원인이 명확히 구분될 필요가 생기면, 인증·요청 형식·지원 파일 오류를 별도 OCR 오류 코드로 세분화하는 방안을 검토합니다.

### ⚠️ OCR Adapter 직접 테스트가 현재 없음

현재 테스트는 트레이너 신청 서비스에서 `OcrUseCase`를 Mock으로 사용하는 방식입니다. Clova multipart 요청 구성, 5xx·네트워크 재시도, 응답 스키마 변환을 독립 검증하는 OCR Adapter 테스트는 확인되지 않았습니다.

권장 검증 범위:

- `MockRestServiceServer` 또는 `MockWebServer`로 multipart `message`·`file`·`X-OCR-SECRET` 검증
- Clova 5xx와 `ResourceAccessException`의 재시도·`@Recover` 검증
- `images` 누락, `inferResult != SUCCESS`, `fields == null`, 빈 `fields` 응답 검증
- `jpg`, `jpeg`, `png`, `pdf` MIME Type·확장자 조합 검증

## 6. 보안·개인정보 주의

- OCR 요청 파일은 자격증 원본이므로 파일 바이트, OCR 추출 전문, Secret Key를 로그에 남기지 않습니다.
- 장애 분석에는 내부 `requestId`, Clova `requestId`, 파일 형식·크기, 실패 원인 태그만 사용합니다.
- 운영 로그·모니터링 화면을 공유할 때도 자격증에 포함된 이름·번호 등 개인정보가 포함되지 않았는지 확인합니다.

## 📝 문서 정보

- 업데이트일: `2026-07-21`
- 변경 사항(요약):
  - OCR 오류 코드별 원인·점검 방법·대응 절차를 정리했습니다. 🛠️
  - Clova 4xx의 502 변환과 OCR Adapter 직접 테스트 부재를 현재 유의 사항으로 기록했습니다. ⚠️
  - 설정·로그·메트릭 점검 시 Secret Key와 자격증 개인정보를 보호하는 기준을 추가했습니다. 🔒
