package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrainerApplicationMetric {

    private static final String OUTCOME_SUCCESS = "success";
    private static final String OUTCOME_FAILURE = "failure";
    private static final String UNKNOWN = "unknown";

    private final MeterRegistry meterRegistry;

    public TrainerApplicationMetric(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Timer.Sample : 측정 시작 시점의 상태를 들고 있다가,
    // 아래 record 메서드에서 stop() 호출 시 실제 소요 시간을 기록한다.
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    /*
     * 트레이너 신청 전체 처리 시간
     * 예: create, update, approve, reject, cancel
     */
    public void recordApplicationDuration(
            Timer.Sample sample,
            String operation,
            String outcome
    ) {
        sample.stop(
                Timer.builder("gymjjak.trainer.application.duration")
                        .description("트레이너 신청 처리 시간")
                        .tag("operation", normalizeOperation(operation))
                        .tag("outcome", normalizeOutcome(outcome))
                        .register(meterRegistry)
        );
    }

    /*
     * 트레이너 신청 과정에서 파일 도메인에 파일 메타데이터를 등록하는 시간
     * file_type은 PROFILE_IMAGE, CERTIFICATION처럼 낮은 cardinality 값만 사용
     */
    public void recordFileRegisterDuration(
            Timer.Sample sample,
            String fileGroup,
            String outcome
    ) {
        sample.stop(
                Timer.builder("gymjjak.trainer.application.file.register.duration")
                        .description("트레이너 신청 파일 등록 처리 시간")
                        .tag("file_group", normalizeFileGroup(fileGroup))
                        .tag("outcome", normalizeOutcome(outcome))
                        .register(meterRegistry)
        );
    }

    /*
     * OCR 검증을 위해 자격증 파일을 File 도메인에서 다운로드하는 시간
     * S3 또는 File 도메인 병목을 분리해서 보기 위한 메트릭
     */
    public void recordCertificateDownloadDuration(
            Timer.Sample sample,
            String outcome
    ) {
        sample.stop(
                Timer.builder("gymjjak.trainer.application.certificate.download.duration")
                        .description("트레이너 신청 자격증 파일 다운로드 처리 시간")
                        .tag("outcome", normalizeOutcome(outcome))
                        .register(meterRegistry)
        );
    }

    /*
     * 자격증 OCR 추출 및 필수 자격증 검증 처리 시간
     * 외부 OCR API 영향이 큰 구간이므로 별도 Timer로 분리
     */
    public void recordOcrValidationDuration(
            Timer.Sample sample,
            String outcome
    ) {
        sample.stop(
                Timer.builder("gymjjak.trainer.application.ocr.validation.duration")
                        .description("트레이너 신청 자격증 OCR 검증 처리 시간")
                        .tag("outcome", normalizeOutcome(outcome))
                        .register(meterRegistry)
        );
    }

    /*
     * 트레이너 신청 도메인 생성 후 DB에 저장하는 시간
     * DB save 병목 여부를 확인하기 위한 메트릭
     */
    public void recordDbSaveDuration(
            Timer.Sample sample,
            String operation,
            String outcome
    ) {
        sample.stop(
                Timer.builder("gymjjak.trainer.application.db.save.duration")
                        .description("트레이너 신청 DB 저장 처리 시간")
                        .tag("operation", normalizeOperation(operation))
                        .tag("outcome", normalizeOutcome(outcome))
                        .register(meterRegistry)
        );
    }

    // safe record 메서드
    public void recordApplicationDurationSafely(
            Timer.Sample sample,
            String operation,
            String outcome
    ) {
        try {
            recordApplicationDuration(sample, operation, outcome);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=trainer_application_metric_record_failed, metric=application_duration, operation={}",
                    operation,
                    exception
            );
        }
    }

    public void recordFileRegisterDurationSafely(
            Timer.Sample sample,
            String fileGroup,
            String outcome
    ) {
        try {
            recordFileRegisterDuration(sample, fileGroup, outcome);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=trainer_application_metric_record_failed, metric=file_register_duration, fileGroup={}",
                    fileGroup,
                    exception
            );
        }
    }

    public void recordCertificateDownloadDurationSafely(
            Timer.Sample sample,
            String outcome
    ) {
        try {
            recordCertificateDownloadDuration(sample, outcome);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=trainer_application_metric_record_failed, metric=certificate_download_duration",
                    exception
            );
        }
    }

    public void recordOcrValidationDurationSafely(
            Timer.Sample sample,
            String outcome
    ) {
        try {
            recordOcrValidationDuration(sample, outcome);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=trainer_application_metric_record_failed, metric=ocr_validation_duration",
                    exception
            );
        }
    }

    public void recordDbSaveDurationSafely(
            Timer.Sample sample,
            String operation,
            String outcome
    ) {
        try {
            recordDbSaveDuration(sample, operation, outcome);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=trainer_application_metric_record_failed, metric=db_save_duration, operation={}",
                    operation,
                    exception
            );
        }
    }

    public String success() {
        return OUTCOME_SUCCESS;
    }

    public String failure() {
        return OUTCOME_FAILURE;
    }

    // operation tag에 넣을 값들
    private String normalizeOperation(String operation) {
        if ("create".equals(operation)) {
            return "create";
        }

        if ("update".equals(operation)) {
            return "update";
        }

        if ("approve".equals(operation)) {
            return "approve";
        }

        if ("reject".equals(operation)) {
            return "reject";
        }

        if ("cancel".equals(operation)) {
            return "cancel";
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

    private String normalizeFileGroup(String fileGroup) {
        if ("certification".equals(fileGroup)) {
            return "certification";
        }

        if ("profile_image_certification".equals(fileGroup)) {
            return "profile_image_certification";
        }

        return UNKNOWN;
    }
}
