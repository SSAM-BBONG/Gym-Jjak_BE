package com.ssambbong.gymjjak.report.domain.model;

import com.ssambbong.gymjjak.report.domain.exception.InvalidReportGroupRelationException;
import com.ssambbong.gymjjak.report.domain.exception.ReportAlreadyProcessedException;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Report {

    private final Long reportId;
    private final Long reportGroupId;
    private final Long reporterId;
    private ReportReasonType reason;
    private String detail;
    private ReportStatus status;
    private Long processedBy;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;

    public static Report reconstruct(
            Long reportId,
            Long reportGroupId,
            Long reporterId,
            ReportReasonType reason,
            String detail,
            ReportStatus status,
            Long processedBy,
            LocalDateTime processedAt,
            LocalDateTime createdAt
    ) {
        return new Report(
                reportId,
                reportGroupId,
                reporterId,
                reason,
                detail,
                status,
                processedBy,
                processedAt,
                createdAt
        );
    }

    public static Report create() {
        return null;
    }

    public void approve(Long adminId, LocalDateTime now) {
        validatePending();
        this.status = ReportStatus.APPROVED;
        this.processedBy = adminId;
        this.processedAt = now;
    }

    public void reject(Long adminId, LocalDateTime now) {
        validatePending();
        this.status = ReportStatus.REJECTED;
        this.processedBy = adminId;
        this.processedAt = now;
    }

    private void validatePending() {
        if (this.status != ReportStatus.PENDING) {
            throw new ReportAlreadyProcessedException(this.reportId);
        }
    }

    public void validateReportGroupId(Long reportGroupId) {
        if (!this.reportGroupId.equals(reportGroupId)) {
            throw new InvalidReportGroupRelationException(reportGroupId, this.reportId);
        }
    }
}
