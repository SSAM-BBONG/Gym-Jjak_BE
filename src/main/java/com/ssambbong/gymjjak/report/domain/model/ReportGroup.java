package com.ssambbong.gymjjak.report.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportGroup {

    private final Long reportGroupId;
    private final String reportNumber;
    private final ReportTargetType targetType;
    private final Long targetId;
    private final Long targetOwnerId;

    private final String snapshotTitle;
    private final String snapshotContent;
    private final String snapshotFileUrl;

    private int totalReportCount;
    private int effectiveReportCount;

    private ReportGroupReviewStatus reviewStatus;
    private ReportGroupSanctionStatus sanctionStatus;

    private Long processedBy;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    // mapStruct 사용용
    public static ReportGroup reconstruct(
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
            Long processedBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        return new ReportGroup(
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
                processedBy,
                createdAt,
                updatedAt,
                deletedAt
        );
    }
}