package com.ssambbong.gymjjak.report.infrastructure.metrics;

import com.ssambbong.gymjjak.report.domain.exception.*;
import com.ssambbong.gymjjak.report.domain.model.ReportStatus;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ReportMetric {

    private final MeterRegistry meterRegistry;

    // 신고 생성 카운터
    private final Counter createReportCounter;

    // 신고 승인 카운터
    private final Counter approveReportCounter;

    // 신고 반려 카운터
    private final Counter rejectReportCounter;

    public  ReportMetric(
            MeterRegistry meterRegistry,
            ReportRepository reportRepository) {
        this.meterRegistry = meterRegistry;

        this.createReportCounter = Counter.builder("report.created")
                        .description("신고 생성 수")
                        .register(meterRegistry);

        this.approveReportCounter = Counter.builder("report.approved")
                .description("승인된 신고 수")
                .register(meterRegistry);

        this.rejectReportCounter = Counter.builder("report.rejected")
                .description("반려된 신고 수")
                .register(meterRegistry);

        // 대기 중인 개별 신고 수
        Gauge.builder(
                        "report.pending",
                        reportRepository,
                        repository -> repository.countByStatus(ReportStatus.PENDING)
                )
        .description("현재 대기 중인 신고 수")
        .register(meterRegistry);
    }

    public void countCreatedReport() {
        createReportCounter.increment();
    }
    public void countApprovedReport() {
        approveReportCounter.increment();
    }
    public void countRejectedReport() {
        rejectReportCounter.increment();
    }

    public void recordFailedReport(String action, RuntimeException exception) {
        Counter.builder("report.failed")
                .description("신고 생성/처리 실패 횟수")
                .tag("action", normalizeAction(action))
                .tag("reason", reportFailureReason(exception))
                .register(meterRegistry)
                .increment();
    }

    private String normalizeAction(String action) {

        if ("create".equals(action)) {
            return "create";
        }
        if ("approve".equals(action)) {
            return "approve";
        }
        if ("reject".equals(action)) {
            return "reject";
        }
        return "unknown";
    }

    private String reportFailureReason(RuntimeException exception) {
        if (exception instanceof SelfReportNotAllowedException) {
            return "self_report_not_allowed"; // 본인 신고 시도
        }

        if (exception instanceof DuplicateReportException) {
            return "duplicate_report"; // 중복 신고 시도
        }

        if (exception instanceof ReportNotFoundException) {
            return "report_not_found"; // 승인/반려 하려는 개별 신고가 없음
        }

        if (exception instanceof ReportGroupNotFoundException) {
            return "report_group_not_found"; // 개별 신고가 속해야 할 신고 그룹이 없음
        }

        if (exception instanceof InvalidReportGroupRelationException) {
            return "invalid_report_group_relation"; // 신고 그룹 ID와 개별 신고의 소속 관계가 맞지 않음
        }

        if (exception instanceof ReportAlreadyProcessedException) {
            return "report_already_processed"; // 이미 승인/반려 처리된 신고를 다시 처리하려 함
        }

        if (exception instanceof ReportGroupCountUnderflowException) {
            return "report_group_count_underflow"; // 반려 처리 중 유효 신고 수가 0 아래로 내려가려는 도메인 오류
        }

        if (exception instanceof UnsupportedOperationException) {
            return "unsupported_report_target"; // 아직 구현되지 않은 신고 대상 타입 요청
        }

        if (exception instanceof IllegalArgumentException) {
            return "invalid_request"; // 잘못된 인자나 요청 값
        }

        return "unexpected"; // 예상하지 못한 런타임 예외
    }
}
