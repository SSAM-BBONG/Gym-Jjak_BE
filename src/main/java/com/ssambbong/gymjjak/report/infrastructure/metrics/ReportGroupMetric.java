package com.ssambbong.gymjjak.report.infrastructure.metrics;

import com.ssambbong.gymjjak.report.domain.exception.*;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ReportGroupMetric {

    private final MeterRegistry meterRegistry;
    // 신고 그룹 생성 카운터
    private final Counter createReportGroupCounter;
    // 신고 그룹 처리 완료 카운터
    private final Counter countCompletedReportGroup;


    public ReportGroupMetric(MeterRegistry meterRegistry,
                             ReportGroupRepository reportGroupRepository
    ) {
        this.meterRegistry = meterRegistry;

        this.createReportGroupCounter = Counter.builder("report.group.created")
                .description("신고 그룹 생성 수")
                .register(meterRegistry);

        this.countCompletedReportGroup = Counter.builder("report.group.resolved")
                .description("신고 그룹 처리 완료 수")
                .register(meterRegistry);

        /* Comment
        *   Gauge : 현재 상태값
        *   사용 예 : 동시 접속자 수, 큐 길이 등
         * */
        // 대기 중인 신고 그룹 수
        Gauge.builder(
                "report.group.pending",
                reportGroupRepository,
                repository -> repository.countByReviewStatusAndDeletedAtIsNull(ReportGroupReviewStatus.PENDING)
        )
        .description("현재 대기 중인 신고 그룹 수")
        .register(meterRegistry);
    }

    public void countCreatedReportGroup() {
        createReportGroupCounter.increment();
    }

    public void countCompletedReportGroup() {
        countCompletedReportGroup.increment();
    }

//    public void recordFailedReportGroupProcessing(String action, RuntimeException exception) {
//        Counter.builder("report.group.processing.failed")
//                .description("신고 그룹 처리 실패 횟수")
//                .tag("action", normalizeAction(action))
//                .tag("reason", reportGroupProcessingFailureReason(exception))
//                .register(meterRegistry)
//                .increment();
//    }

    // 승인, 반려 구분하기 위한 태그
//    private String normalizeAction(String action) {
//        if ("approve".equals(action)) {
//            return "approve";
//        }
//        if ("reject".equals(action)) {
//            return "reject";
//        }
//        return "unknown";
//    }

    // 실패 원인 : 태그 값들, exception 클래스를 기준으로 함
//    private String reportGroupProcessingFailureReason(RuntimeException exception) {
//        if (exception instanceof ReportGroupNotFoundException) {
//            return "report_group_not_found";
//        }
//
//        if (exception instanceof ReportNotFoundException) {
//            return "report_not_found";
//        }
//
//        if (exception instanceof InvalidReportGroupRelationException) {
//            return "invalid_report_group_relation";
//        }
//
//        if (exception instanceof ReportAlreadyProcessedException) {
//            return "already_processed";
//        }
//
//        if (exception instanceof ReportGroupCountUnderflowException) {
//            return "report_group_count_underflow";
//        }
//
//        if (exception instanceof IllegalArgumentException) {
//            return "invalid_request";
//        }
//
//        return "unexpected";
//    }
}
