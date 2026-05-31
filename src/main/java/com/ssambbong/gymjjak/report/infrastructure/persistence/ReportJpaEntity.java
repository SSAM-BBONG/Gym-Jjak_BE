package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.CreatedAtEntity;
import com.ssambbong.gymjjak.report.domain.model.Report;
import com.ssambbong.gymjjak.report.domain.model.ReportReasonType;
import com.ssambbong.gymjjak.report.domain.model.ReportStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reports")
public class ReportJpaEntity extends CreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private Long reportGroupId;

    @Column(nullable = false)
    private Long reporterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportReasonType reason;

    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportStatus status;

    private Long processedBy;

    private LocalDateTime processedAt;

    private ReportJpaEntity(
            Long reportId,
            Long reportGroupId,
            Long reporterId,
            ReportReasonType reason,
            String detail,
            ReportStatus status,
            Long processedBy,
            LocalDateTime processedAt
    ) {
        this.reportId = reportId;
        this.reportGroupId = reportGroupId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.detail = detail;
        this.status = status;
        this.processedBy = processedBy;
        this.processedAt = processedAt;
    }

    public static ReportJpaEntity of(
            Long reportId,
            Long reportGroupId,
            Long reporterId,
            ReportReasonType reason,
            String detail,
            ReportStatus status,
            Long processedBy,
            LocalDateTime processedAt
    ) {
        return new ReportJpaEntity(
                reportId,
                reportGroupId,
                reporterId,
                reason,
                detail,
                status,
                processedBy,
                processedAt
        );
    }

    public void updateFromDomain(Report report) {
        this.reportGroupId = report.getReportGroupId();
        this.reporterId = report.getReporterId();
        this.reason = report.getReason();
        this.detail = report.getDetail();
        this.status = report.getStatus();
        this.processedBy = report.getProcessedBy();
        this.processedAt = report.getProcessedAt();
    }
}
