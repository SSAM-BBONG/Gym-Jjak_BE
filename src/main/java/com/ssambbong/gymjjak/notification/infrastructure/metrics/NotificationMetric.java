package com.ssambbong.gymjjak.notification.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationMetric {

    private static final String OUTCOME_SUCCESS = "success";
    private static final String OUTCOME_FAILURE = "failure";
    private static final String UNKNOWN = "unknown";

    private final MeterRegistry meterRegistry;

    public NotificationMetric(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    //알림 조회 계열 API 처리 시간, 목록 조회 측정
    public void recordQueryDuration(
            Timer.Sample sample,
            String operation,
            String outcome
    ) {
        sample.stop(
                Timer.builder("gymjjak.notification.query.duration")
                        .description("알림 조회 처리 시간")
                        .tag("operation", normalizeQueryOperation(operation))
                        .tag("outcome", normalizeOutcome(outcome))
                        .register(meterRegistry)
        );
    }


    // 알림 command API 처리 시간 예: create, read, delete
    public void recordCommandDuration(
            Timer.Sample sample,
            String operation,
            String outcome
    ) {
        sample.stop(
                Timer.builder("gymjjak.notification.command.duration")
                        .description("알림 명령 처리 시간")
                        .tag("operation", normalizeCommandOperation(operation))
                        .tag("outcome", normalizeOutcome(outcome))
                        .register(meterRegistry)
        );
    }

    // 읽음 처리 요청에 포함된 알림 개수 분포
    // 예: 한 번에 1개 읽음, 10개 읽음, 100개 읽음
    public void recordReadRequestedItems(int count) {
        recordItemSummary(
                "gymjjak.notification.read.requested.items",
                "알림 읽음 요청 개수",
                count
        );
    }

    // 실제 읽음 처리된 알림 개수 분포
    public void recordReadProcessedItems(int count) {
        recordItemSummary(
                "gymjjak.notification.read.processed.items",
                "알림 읽음 처리 개수",
                count
        );
    }

    // 삭제 요청에 포함된 알림 개수 분포
    public void recordDeleteRequestedItems(int count) {
        recordItemSummary(
                "gymjjak.notification.delete.requested.items",
                "알림 삭제 요청 개수",
                count
        );
    }

    // 실제 삭제 처리된 알림 개수 분포
    public void recordDeleteProcessedItems(int count) {
        recordItemSummary(
                "gymjjak.notification.delete.processed.items",
                "알림 삭제 처리 개수",
                count
        );
    }

    // 실시간 알림 전송 처리 시간
    public void recordRealtimeSendDuration(
            Timer.Sample sample,
            String outcome
    ) {
        sample.stop(
                Timer.builder("gymjjak.notification.realtime.send.duration")
                        .description("알림 실시간 전송 처리 시간")
                        .tag("outcome", normalizeOutcome(outcome))
                        .register(meterRegistry)
        );
    }

    // 실시간 알림 전송 성공 횟수
    public void countRealtimeSent() {
        Counter.builder("gymjjak.notification.realtime.sent.total")
                .description("알림 실시간 전송 성공 횟수")
                .register(meterRegistry)
                .increment();
    }

    // 실시간 알림 전송 실패 횟수
    public void countRealtimeFailed(String reason) {
        Counter.builder("gymjjak.notification.realtime.failed.total")
                .description("알림 실시간 전송 실패 횟수")
                .tag("reason", normalizeRealtimeFailureReason(reason))
                .register(meterRegistry)
                .increment();
    }

    public void recordCommandDurationSafely(
            Timer.Sample sample,
            String operation,
            String outcome
    ) {
        try {
            recordCommandDuration(sample, operation, outcome);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=notification_metric_record_failed, metric=command_duration, operation={}",
                    operation,
                    exception
            );
        }
    }

    public void recordQueryDurationSafely(
            Timer.Sample sample,
            String operation,
            String outcome
    ) {
        try {
            recordQueryDuration(sample, operation, outcome);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=notification_metric_record_failed, metric=query_duration, operation={}",
                    operation,
                    exception
            );
        }
    }

    public void recordReadItemsSafely(
            int requestedCount,
            int processedCount
    ) {
        try {
            recordReadRequestedItems(requestedCount);
            recordReadProcessedItems(processedCount);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=notification_metric_record_failed, metric=read_items",
                    exception
            );
        }
    }

    public void recordDeleteItemsSafely(
            int requestedCount,
            int processedCount
    ) {
        try {
            recordDeleteRequestedItems(requestedCount);
            recordDeleteProcessedItems(processedCount);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=notification_metric_record_failed, metric=delete_items",
                    exception
            );
        }
    }

    public void recordRealtimeSendDurationSafely(
            Timer.Sample sample,
            String outcome
    ) {
        try {
            recordRealtimeSendDuration(sample, outcome);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=notification_metric_record_failed, metric=realtime_send_duration",
                    exception
            );
        }
    }

    public void countRealtimeSentSafely() {
        try {
            countRealtimeSent();
        } catch (RuntimeException exception) {
            log.warn(
                    "event=notification_metric_record_failed, metric=realtime_sent",
                    exception
            );
        }
    }

    public void countRealtimeFailedSafely(String reason) {
        try {
            countRealtimeFailed(reason);
        } catch (RuntimeException exception) {
            log.warn(
                    "event=notification_metric_record_failed, metric=realtime_failed, reason={}",
                    reason,
                    exception
            );
        }
    }

    private String normalizeRealtimeFailureReason(String reason) {
        if ("invalid_argument".equals(reason)) {
            return "invalid_argument";
        }

        if ("send_failed".equals(reason)) {
            return "send_failed";
        }

        return UNKNOWN;
    }

    public String success() {
        return OUTCOME_SUCCESS;
    }

    public String failure() {
        return OUTCOME_FAILURE;
    }

    private void recordItemSummary(
            String metricName,
            String description,
            int count
    ) {
        DistributionSummary.builder(metricName)
                .description(description)
                .register(meterRegistry)
                .record(count);
    }

    private String normalizeQueryOperation(String operation) {
        if ("list".equals(operation)) {
            return "list";
        }

        return UNKNOWN;
    }

    private String normalizeCommandOperation(String operation) {
        if ("create".equals(operation)) {
            return "create";
        }

        if ("read".equals(operation)) {
            return "read";
        }

        if ("delete".equals(operation)) {
            return "delete";
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
