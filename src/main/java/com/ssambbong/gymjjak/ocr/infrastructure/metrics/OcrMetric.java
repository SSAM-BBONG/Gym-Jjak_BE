package com.ssambbong.gymjjak.ocr.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OcrMetric {

    private static final String OUTCOME_SUCCESS = "success";
    private static final String OUTCOME_FAILURE = "failure";
    private static final String UNKNOWN = "unknown";

    private final MeterRegistry meterRegistry;

    public OcrMetric(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public String success() {
        return OUTCOME_SUCCESS;
    }

    public String failure() {
        return OUTCOME_FAILURE;
    }

    // OCR 추출 유즈케이스 전체 처리 시간 기록 메서드
    public void recordExtractDurationSafely(
            Timer.Sample sample,
            String contentType,
            String outcome
    ) {
        try {
            sample.stop(
                    Timer.builder("gymjjak.ocr.extract.duration")
                            .description("OCR 추출 유즈케이스 처리 시간")
                            .tag("content_type", normalizeContentType(contentType))
                            .tag("outcome", normalizeOutcome(outcome))
                            .register(meterRegistry)
            );
        } catch (RuntimeException exception) {
            log.warn(
                    "event=ocr_metric_record_failed, metric=extract_duration, contentType={}",
                    contentType,
                    exception
            );
        }
    }

    // Clova OCR 외부 API 요청 시간을 provider, 파일 포맷, 결과, 실패 사유 기준 기록
    public void recordExternalRequestDurationSafely(
            Timer.Sample sample,
            String provider,
            String format,
            String outcome,
            String reason
    ) {
        try {
            sample.stop(
                    Timer.builder("gymjjak.ocr.external.request.duration")
                            .description("OCR 외부 API 요청 처리 시간")
                            .tag("provider", normalizeProvider(provider))
                            .tag("format", normalizeFormat(format))
                            .tag("outcome", normalizeOutcome(outcome))
                            .tag("reason", normalizeReason(reason))
                            .register(meterRegistry)
            );
        } catch (RuntimeException exception) {
            log.warn(
                    "event=ocr_metric_record_failed, metric=external_request_duration, provider={}, format={}, reason={}",
                    provider,
                    format,
                    reason,
                    exception
            );
        }
    }

    // @Retryable 재시도 실패한 횟수 provider 기준 기록
    public void countRetryExhaustedSafely(String provider) {
        try {
            Counter.builder("gymjjak.ocr.retry.exhausted.total")
                    .description("OCR 재시도 소진 횟수")
                    .tag("provider", normalizeProvider(provider))
                    .register(meterRegistry)
                    .increment();
        } catch (RuntimeException exception) {
            log.warn(
                    "event=ocr_metric_record_failed, metric=retry_exhausted, provider={}",
                    provider,
                    exception
            );
        }
    }

    // contentType 변환
    private String normalizeContentType(String contentType) {
        if ("image/jpeg".equals(contentType)) {
            return "image_jpeg";
        }

        if ("image/png".equals(contentType)) {
            return "image_png";
        }

        if ("application/pdf".equals(contentType)) {
            return "application_pdf";
        }

        return UNKNOWN;
    }

    // ocr은 clova만 사용
    private String normalizeProvider(String provider) {
        if ("clova".equals(provider)) {
            return "clova";
        }

        return UNKNOWN;
    }

    // 파일 포멧 타입 변환
    private String normalizeFormat(String format) {
        if ("jpg".equals(format)) {
            return "jpg";
        }

        if ("png".equals(format)) {
            return "png";
        }

        if ("pdf".equals(format)) {
            return "pdf";
        }

        return UNKNOWN;
    }

    private String normalizeOutcome(String outcome) {
        if (OUTCOME_SUCCESS.equals(outcome)) {
            return OUTCOME_SUCCESS;
        }

        if (OUTCOME_FAILURE.equals(outcome)) {
            return OUTCOME_FAILURE;
        }

        return UNKNOWN;
    }

    // 실패 사유 자세하게 변환
    private String normalizeReason(String reason) {
        if ("none".equals(reason)) {
            return "none";
        }

        if ("message_serialization_failed".equals(reason)) {
            return "message_serialization_failed";
        }

        if ("external_api_4xx".equals(reason)) {
            return "external_api_4xx";
        }

        if ("external_api_5xx".equals(reason)) {
            return "external_api_5xx";
        }

        if ("network_or_timeout".equals(reason)) {
            return "network_or_timeout";
        }

        if ("external_api_client_error".equals(reason)) {
            return "external_api_client_error";
        }

        if ("invalid_response".equals(reason)) {
            return "invalid_response";
        }

        if ("unsupported_format".equals(reason)) {
            return "unsupported_format";
        }

        if ("retry_exhausted".equals(reason)) {
            return "retry_exhausted";
        }

        return UNKNOWN;
    }
}
