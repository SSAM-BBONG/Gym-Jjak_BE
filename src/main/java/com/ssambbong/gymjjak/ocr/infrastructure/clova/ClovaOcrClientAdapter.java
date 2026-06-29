package com.ssambbong.gymjjak.ocr.infrastructure.clova;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.application.port.OcrClientPort;
import com.ssambbong.gymjjak.ocr.domain.OcrExtractedField;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;
import com.ssambbong.gymjjak.ocr.domain.exception.OcrErrorCode;
import com.ssambbong.gymjjak.ocr.domain.exception.OcrException;
import com.ssambbong.gymjjak.ocr.infrastructure.clova.dto.ClovaOcrMessageRequest;
import com.ssambbong.gymjjak.ocr.infrastructure.clova.dto.ClovaOcrResponse;
import com.ssambbong.gymjjak.ocr.infrastructure.metrics.OcrMetric;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClovaOcrClientAdapter implements OcrClientPort {

    // OCR 호출에 필요한 상수
    private static final String SECRET_HEADER = "X-OCR-SECRET";
    private static final String OCR_SUCCESS = "SUCCESS";

    // 메트릭을 위한 상수 추가
    private static final String PROVIDER_CLOVA = "clova";
    private static final String REASON_NONE = "none";
    private static final String REASON_MESSAGE_SERIALIZATION_FAILED = "message_serialization_failed";
    private static final String REASON_EXTERNAL_API_4XX = "external_api_4xx";
    private static final String REASON_EXTERNAL_API_5XX = "external_api_5xx";
    private static final String REASON_NETWORK_OR_TIMEOUT = "network_or_timeout";
    private static final String REASON_EXTERNAL_API_CLIENT_ERROR = "external_api_client_error";

    // ClovaOcrMessageRequest를 JSON 문자열로 직렬화
    private final ObjectMapper objectMapper;
    // Invoke URL, Secret Key 설정값 보관
    private final ClovaOcrProperties properties;
    // Bean 등록한 RestClient 호출
    private final RestClient clovaOcrRestClient;
    // metric 추가
    private final OcrMetric ocrMetric;

    @Retryable(
            retryFor = ClovaOcrRetryableException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2.0)
    )
    @Override
    public OcrResult extractOcr(ExtractOcrCommand command) {

        // 내부 추적용 Id, 파일 포멧
        String requestId = UUID.randomUUID().toString();
        String format = resolveFormat(command.contentType(), command.originalFilename());
        long startedAt = System.currentTimeMillis();

        Timer.Sample externalRequestTimer = ocrMetric.startTimer();
        String outcome = ocrMetric.success();
        String reason = REASON_NONE;

        log.info(
                "event=ocr_request_started provider=clova requestId={}, contentType={}, fileSize={}, format={}",
                requestId,
                command.contentType(),
                command.fileBytes().length,
                format
        );

        try {

            // ocr 호출해서 response값 담기
            ClovaOcrResponse response = callClova(command, requestId, format);
            // ocr response를 필요한 result 값으로 변환
            OcrResult result = toOcrResult(response);

            log.info(
                    "event=ocr_request_succeeded provider=clova requestId={}, clovaRequestId={}, durationMs={}, templateName={}, fieldCount={}",
                    requestId,
                    response.requestId(),
                    System.currentTimeMillis() - startedAt,
                    result.matchedTemplateName(),
                    result.fields().size()
            );

            return result;
        } catch (JsonProcessingException exception) {
            // 실패 원인 기록
            outcome = ocrMetric.failure();
            reason = REASON_MESSAGE_SERIALIZATION_FAILED;
            // clova 호출시 발생하는 에러, 재시도 x
            log.error(
                    "event=ocr_request_failed provider=clova reason=message_serialization_failed requestId={}, durationMs={}",
                    requestId,
                    System.currentTimeMillis() - startedAt,
                    exception
            );

            throw new OcrException(OcrErrorCode.OCR_REQUEST_FAILED, exception);

        } catch (RestClientResponseException exception) {
            outcome = ocrMetric.failure();
            // 500 서버 에러, 재시도 o
            if (exception.getStatusCode().is5xxServerError()) {
                reason = REASON_EXTERNAL_API_5XX;

                log.warn(
                    "event=ocr_request_retryable_failed provider=clova reason=external_api_5xx requestId={}, statusCode={}, durationMs={}, responseBodyLength={}",
                    requestId,
                    exception.getStatusCode().value(),
                    System.currentTimeMillis() - startedAt,
                    exception.getResponseBodyAsString().length()
                );

                throw new ClovaOcrRetryableException("Clova OCR 5xx response", exception);
        }

            reason = REASON_EXTERNAL_API_4XX;
            // 4xx 클라이언트 오류 (파일 포멧 실패, 잘못된 요청 등), 재시도 x
            log.warn(
                "event=ocr_request_failed provider=clova reason=external_api_4xx requestId={}, statusCode={}, durationMs={}, responseBodyLength={}",
                requestId,
                exception.getStatusCode().value(),
                System.currentTimeMillis() - startedAt,
                exception.getResponseBodyAsString().length()
            );

            throw new OcrException(OcrErrorCode.OCR_REQUEST_FAILED, exception);

    } catch (ResourceAccessException exception) {
            outcome = ocrMetric.failure();
            reason = REASON_NETWORK_OR_TIMEOUT;
            // 네트워크 타임 아웃 에러, 재시도 o
            log.warn(
                "event=ocr_request_retryable_failed provider=clova reason=network_or_timeout requestId={}, durationMs={}",
                requestId,
                System.currentTimeMillis() - startedAt
             );

        throw new ClovaOcrRetryableException("Clova OCR network or timeout error", exception);

    } catch (RestClientException exception) {
            outcome = ocrMetric.failure();
            reason = REASON_EXTERNAL_API_CLIENT_ERROR;
            //  그 외 RestClient 계열 오류, 재시도x
            log.error(
                "event=ocr_request_failed provider=clova reason=external_api_client_error requestId={}, durationMs={}",
                requestId,
                System.currentTimeMillis() - startedAt,
                exception
            );

            throw new OcrException(OcrErrorCode.OCR_REQUEST_FAILED, exception);
        } catch (OcrException exception) {
            outcome = ocrMetric.failure();
            reason = "invalid_response";
            throw exception;
        } finally {
            ocrMetric.recordExternalRequestDurationSafely(
                    externalRequestTimer,
                    PROVIDER_CLOVA,
                    format,
                    outcome,
                    reason
            );
        }
    }

    // clova ocr 호출
    private ClovaOcrResponse callClova(
            ExtractOcrCommand command,
            String requestId,
            String format
    ) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(
                ClovaOcrMessageRequest.of(requestId, format)
        );
        // multipart body 생성
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        // OCR 요청 json
        bodyBuilder.part("message", message)
                .contentType(MediaType.APPLICATION_JSON);
        // 실제 이미지 파일
        bodyBuilder.part(
                        "file",
                        new OcrFileResource(command.fileBytes(), command.originalFilename())
                )
                .contentType(resolveMediaType(command.contentType()));

        return clovaOcrRestClient.post()
                .uri(properties.invokeUrl())
                .header(SECRET_HEADER, properties.secretKey())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(bodyBuilder.build())
                .retrieve()
                .body(ClovaOcrResponse.class);
    }

    // clova response 값을 도메인 객체로 변환
    private OcrResult toOcrResult(ClovaOcrResponse response) {

        // null 검증
        if (response == null || response.images() == null || response.images().isEmpty()) {
            throw new OcrException(OcrErrorCode.OCR_INVALID_RESPONSE);
        }

        // 1번째 이미지 결과 선택
        ClovaOcrResponse.ClovaOcrImageResponse image = response.images().get(0);

        // 성공 여부 검증
        if (!OCR_SUCCESS.equals(image.inferResult())) {
            throw new OcrException(
                    OcrErrorCode.OCR_INVALID_RESPONSE,
                    "OCR 이미지 분석 결과가 성공이 아닙니다."
            );
        }

        // fields 검증, 비어있어도 사용자가 직접 입력할 수 있게 empty는 허용
        if (image.fields() == null) {
            throw new OcrException(OcrErrorCode.OCR_INVALID_RESPONSE);
        }

        String matchedTemplateName = image.matchedTemplate() == null
                ? null
                : image.matchedTemplate().name();

        // ocr 전용 dto -> 우리 도메인 객체로 변환
        // 추후 ocr 변환해도 Response만 바꿔주면 유지 가능함 ⭐
        List<OcrExtractedField> fields = image.fields().stream()
                .map(field -> new OcrExtractedField(
                        field.name(),
                        field.inferText(),
                        field.inferConfidence()
                ))
                .toList();

        return new OcrResult(matchedTemplateName, fields);
    }

    // format 확장자 값 결정
    private String resolveFormat(String contentType, String originalFilename) {
        if (MediaType.IMAGE_JPEG_VALUE.equals(contentType)) {
            return "jpg";
        }

        if (MediaType.IMAGE_PNG_VALUE.equals(contentType)) {
            return "png";
        }

        if (MediaType.APPLICATION_PDF_VALUE.equals(contentType)) {
            return "pdf";
        }

        if (originalFilename != null && originalFilename.contains(".")) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
                    .toLowerCase();

            if ("jpg".equals(extension) || "jpeg".equals(extension)) {
                return "jpg";
            }

            if ("png".equals(extension)) {
                return "png";
            }

            if ("pdf".equals(extension)) {
                return "pdf";
            }
        }

        throw new OcrException(OcrErrorCode.OCR_UNSUPPORTED_FILE_FORMAT);
    }

    // 파일 파트의 Content-Type 정하는 메서드
    private MediaType resolveMediaType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(contentType);
        } catch (IllegalArgumentException exception) {
            // 잘못된 문자열 들어오면 지원하지 않는 파일 예외 처리하기
            throw new OcrException(OcrErrorCode.OCR_UNSUPPORTED_FILE_FORMAT, exception);
        }
    }

    // getFilename() override 해서 파일명 제공하는 메서드
    private static class OcrFileResource extends ByteArrayResource {

        private final String filename;

        private OcrFileResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename == null || filename.isBlank()
                    ? "ocr-image"
                    : filename;
        }
    }

    // 3번 재시도 실패 했을 때, 자동 호출되는 에러
    @Recover
    public OcrResult recover(
            ClovaOcrRetryableException exception,
            ExtractOcrCommand command
    ) {
        // recover 실패했을 때, 기록
        ocrMetric.countRetryExhaustedSafely(PROVIDER_CLOVA);

        log.error(
                "event=ocr_request_retries_exhausted provider=clova reason=retry_exhausted contentType={}, fileSize={}",
                command.contentType(),
                command.fileBytes() == null ? 0 : command.fileBytes().length,
                exception
        );

        throw new OcrException(OcrErrorCode.OCR_REQUEST_FAILED, exception);
    }
}