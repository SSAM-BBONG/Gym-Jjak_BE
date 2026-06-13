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
    private int effectiveReportCount; // 승인된 유효 신고 수

    private ReportGroupReviewStatus reviewStatus; // 검토 상태
    private ReportGroupSanctionStatus sanctionStatus; // 제재 상태

    private Long processedBy;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    // mapStruct 사용용 DB -> 도메인으로 복원하는 도메인
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

    // 새 신고 그룹 등록 시 사용하는 메서드
    public static ReportGroup create(
            String reportNumber,
            ReportTargetType targetType,
            Long targetId,
            Long targetOwnerId,
            String snapshotTitle,
            String snapshotContent,
            String snapshotFileUrl,
            LocalDateTime now
    ) {
        return new ReportGroup(
                null,
                reportNumber,
                targetType,
                targetId,
                targetOwnerId,
                snapshotTitle,
                snapshotContent,
                snapshotFileUrl,
                1,
                1,
                ReportGroupReviewStatus.PENDING,
                ReportGroupSanctionStatus.NONE,
                null,
                now,
                now,
                null
        );
    }

    // 누적 신고 수,
    public void registerNewReport() {
        this.totalReportCount++;
        this.effectiveReportCount++;
        this.reviewStatus = ReportGroupReviewStatus.PENDING;
    }

    // 상세 리뷰 상태에 따른 신고 그룹 검토 상태 값 재계산
    public void recalculateReviewStatus(List<Report> reports) {
        // 단건 신고 하나라도 대기 -> pending
        boolean hasPending = reports.stream()
                .anyMatch(report -> report.getStatus() == ReportStatus.PENDING);
        if (hasPending) {
            this.reviewStatus = ReportGroupReviewStatus.PENDING;
            return;
        }
        // 단건 신고 처리 후 1개라도 approve 있으면 resolved(해결)
        boolean hasApproved = reports.stream()
                .anyMatch(report -> report.getStatus() == ReportStatus.APPROVED);
        if (hasApproved) {
            this.reviewStatus = ReportGroupReviewStatus.RESOLVED;
            return;
        }
        // 대기x, 승인x -> 해당 신고 그룹 반려로 처리
        this.reviewStatus = ReportGroupReviewStatus.REJECTED;
    }

    // 자동 임시 블라인드 대상 도메인 반펼
    public boolean isAutoBlindTarget() {
        return this.targetType == ReportTargetType.PT_COURSE ||
                this.targetType == ReportTargetType.POST ||
                this.targetType == ReportTargetType.COMMENT;
    }

    // 승인 누적 5개 기반 제재 상태 동기화 트리거 규칙
    public void syncAutoSanctionStatus() {
        // 제재 확정은 리턴
        if (this.sanctionStatus == ReportGroupSanctionStatus.MANUAL_BLINDED) {
            return;
        }

        // 피드백, 리뷰 or none 상태는 리턴
        if (!isAutoBlindTarget()) {
            this.sanctionStatus = ReportGroupSanctionStatus.NONE;
            return;
        }

        // 유효 신고 수 5 이상이면 자동 블라인드
        // TODO : 이걸 MANUAL_BLINDED 로 바꿔야되지 않나?
        if (this.effectiveReportCount >= 5) {
            this.sanctionStatus = ReportGroupSanctionStatus.AUTO_BLINDED;
            return;
        }
        // 미만이면 NONE으로
        this.sanctionStatus = ReportGroupSanctionStatus.NONE;
    }

    public void markProcessedBy(Long adminId) {
        this.processedBy = adminId;
    }

    // 반려 처리 시 유효 수 빼는 규칙
    public void decreaseEffectiveReportCount() {
        // 신고수 0 이하면 예외처리
        if (this.effectiveReportCount <= 0) {
            throw new ReportGroupCountUnderflowException(
                    this.reportGroupId, this.effectiveReportCount);
        }
        this.effectiveReportCount--;
    }

    public void manuallyBlind(Long adminId) {
        this.reviewStatus = ReportGroupReviewStatus.RESOLVED;
        this.sanctionStatus = ReportGroupSanctionStatus.MANUAL_BLINDED;
        this.processedBy = adminId;
    }
}