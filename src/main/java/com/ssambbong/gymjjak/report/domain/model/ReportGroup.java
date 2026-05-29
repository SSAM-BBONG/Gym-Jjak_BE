package com.ssambbong.gymjjak.report.domain.model;

import com.ssambbong.gymjjak.report.domain.exception.ReportGroupCountUnderflowException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

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

    // 상세 리뷰 처리에 따른 신고 그룹 검토 상태 값 재계산
    public void recalculateReviewStatus(List<Report> reports) {

        boolean hasPending = reports.stream()
                .anyMatch(report -> report.getStatus() == ReportStatus.PENDING);

        if (hasPending) {
            this.reviewStatus = ReportGroupReviewStatus.PENDING;
            return;
        }

        boolean hasApproved = reports.stream()
                .anyMatch(report -> report.getStatus() == ReportStatus.APPROVED);

        if (hasApproved) {
            this.reviewStatus = ReportGroupReviewStatus.RESOLVED;
            return;
        }

        this.reviewStatus = ReportGroupReviewStatus.REJECTED;
    }

    // 자동 제재 타입 검증
    public boolean isAutoBlindTarget() {
        return this.targetType == ReportTargetType.PT_COURSE ||
                this.targetType == ReportTargetType.POST ||
                this.targetType == ReportTargetType.COMMENT;
    }

    // 검토 상태 재계산 로직
    public void syncAutoSanctionStatus() {
        if (this.sanctionStatus == ReportGroupSanctionStatus.MANUAL_BLINDED) {
            return;
        }

        if (!isAutoBlindTarget()) {
            this.sanctionStatus = ReportGroupSanctionStatus.NONE;
            return;
        }

        if (this.effectiveReportCount >= 5) {
            this.sanctionStatus = ReportGroupSanctionStatus.AUTO_BLINDED;
            return;
        }

        this.sanctionStatus = ReportGroupSanctionStatus.NONE;
    }

    public void markProcessedBy(Long adminId) {
        this.processedBy = adminId;
    }

    // 유효 신고 수 감소 메서드
    public void decreaseEffectiveReportCount() {
        if (this.effectiveReportCount <= 0) {
            throw new ReportGroupCountUnderflowException(
                    this.reportGroupId, this.effectiveReportCount);
        }
        this.effectiveReportCount--;
    }
}