package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseTimeEntity;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "report_groups")
public class ReportGroupJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportGroupId;

    @Column(nullable = false, length = 20, unique = true)
    private String reportNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportTargetType targetType;

    @Column(nullable = false)
    private Long targetId;

    private Long targetOwnerId;

    @Column(length = 255)
    private String snapshotTitle;

    @Lob
    private String snapshotContent;

    @Column(length = 500)
    private String snapshotFileUrl;

    @Column(nullable = false)
    private int totalReportCount;

    @Column(nullable = false)
    private int effectiveReportCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportGroupReviewStatus reviewStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportGroupSanctionStatus sanctionStatus;

    private Long processedBy;

    private ReportGroupJpaEntity(
            Long reportGroupId,
            String reportNumber,
            ReportTargetType targetType,
            Long targetId,
            Long targetOwnerId,
            String snapshotTitle,
            String snapshotContent,
            String snapshotFileUrl,
            int totalReportCount,
            int effectiveReportCount,
            ReportGroupReviewStatus reviewStatus,
            ReportGroupSanctionStatus sanctionStatus,
            Long processedBy
    ) {
        this.reportGroupId = reportGroupId;
        this.reportNumber = reportNumber;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetOwnerId = targetOwnerId;
        this.snapshotTitle = snapshotTitle;
        this.snapshotContent = snapshotContent;
        this.snapshotFileUrl = snapshotFileUrl;
        this.totalReportCount = totalReportCount;
        this.effectiveReportCount = effectiveReportCount;
        this.reviewStatus = reviewStatus;
        this.sanctionStatus = sanctionStatus;
        this.processedBy = processedBy;
    }

    public static ReportGroupJpaEntity of(
            Long reportGroupId,
            String reportNumber,
            ReportTargetType targetType,
            Long targetId,
            Long targetOwnerId,
            String snapshotTitle,
            String snapshotContent,
            String snapshotFileUrl,
            int totalReportCount,
            int effectiveReportCount,
            ReportGroupReviewStatus reviewStatus,
            ReportGroupSanctionStatus sanctionStatus,
            Long processedBy
    ) {
        return new ReportGroupJpaEntity(
                reportGroupId,
                reportNumber,
                targetType,
                targetId,
                targetOwnerId,
                snapshotTitle,
                snapshotContent,
                snapshotFileUrl,
                totalReportCount,
                effectiveReportCount,
                reviewStatus,
                sanctionStatus,
                processedBy
        );
    }

    public void updateFromDomain(ReportGroup reportGroup) {
        this.reportNumber = reportGroup.getReportNumber();
        this.targetType = reportGroup.getTargetType();
        this.targetId = reportGroup.getTargetId();
        this.targetOwnerId = reportGroup.getTargetOwnerId();
        this.snapshotTitle = reportGroup.getSnapshotTitle();
        this.snapshotContent = reportGroup.getSnapshotContent();
        this.snapshotFileUrl = reportGroup.getSnapshotFileUrl();
        this.totalReportCount = reportGroup.getTotalReportCount();
        this.effectiveReportCount = reportGroup.getEffectiveReportCount();
        this.reviewStatus = reportGroup.getReviewStatus();
        this.sanctionStatus = reportGroup.getSanctionStatus();
        this.processedBy = reportGroup.getProcessedBy();
    }
}
