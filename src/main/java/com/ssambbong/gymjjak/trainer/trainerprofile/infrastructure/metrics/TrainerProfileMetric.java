package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrainerProfileMetric {

    private static final String OUTCOME_SUCCESS = "success";
    private static final String OUTCOME_FAILURE = "failure";
    private static final String UNKNOWN = "unknown";

    private final MeterRegistry meterRegistry;

    public TrainerProfileMetric(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    // 트레이너 프로필 조회 계열 API 처리 시간 : search, public_detail, my_detail
    public void recordQueryDuration(
            Timer.Sample sample,
            String operation,
            boolean keywordPresent,
            String outcome
    ) {
        sample.stop(
                Timer.builder("gymjjak.trainer.profile.query.duration")
                        .description("트레이너 프로필 조회 처리 시간")
                        .tag("operation", normalizeOperation(operation))
                        .tag("keyword_present", Boolean.toString(keywordPresent))
                        .tag("outcome", normalizeOutcome(outcome))
                        .register(meterRegistry)
        );
    }

    // safe record 메서드
    public void recordQueryDurationSafely(
            Timer.Sample sample,
            String operation,
            boolean keywordPresent,
            String outcome
    ) {
        try {
            recordQueryDuration(sample, operation, keywordPresent, outcome);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=trainer_profile_metric_record_failed, metric=query_duration, operation={}",
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

    private String normalizeOperation(String operation) {
        if ("search".equals(operation)) {
            return "search";
        }

        if ("public_detail".equals(operation)) {
            return "public_detail";
        }

        if ("my_detail".equals(operation)) {
            return "my_detail";
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
}
