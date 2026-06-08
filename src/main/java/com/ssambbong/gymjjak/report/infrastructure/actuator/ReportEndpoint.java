package com.ssambbong.gymjjak.report.infrastructure.actuator;

import com.ssambbong.gymjjak.report.domain.model.ReportStatus;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "report")
@RequiredArgsConstructor
public class ReportEndpoint {

    private final ReportRepository reportRepository;

    @ReadOperation
    public ReportSummary summary() {
        return new ReportSummary(
                reportRepository.countAll(),
                reportRepository.countByStatus(ReportStatus.PENDING),
                reportRepository.countByStatus(ReportStatus.APPROVED),
                reportRepository.countByStatus(ReportStatus.REJECTED)
        );
    }

    public record ReportSummary(
            long totalCount, // 총 신고 수
            long pendingCount, // 대기중인 수
            long approvedCount, // 승인된 산고 수
            long rejectedCount // 반려된 신고 수
    ) {
    }
}
