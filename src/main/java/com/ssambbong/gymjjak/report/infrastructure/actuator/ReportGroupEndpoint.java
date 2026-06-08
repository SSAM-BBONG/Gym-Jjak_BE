package com.ssambbong.gymjjak.report.infrastructure.actuator;

import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "reportgroup")
@RequiredArgsConstructor
public class ReportGroupEndpoint {

    private final ReportGroupRepository reportGroupRepository;

    @ReadOperation
    public ReportGroupSummary summary() {
        return new ReportGroupSummary(
                reportGroupRepository.countAllByDeletedAtIsNull(),
                reportGroupRepository.countByReviewStatusAndDeletedAtIsNull(ReportGroupReviewStatus.PENDING),
                reportGroupRepository.countByReviewStatusAndDeletedAtIsNull(ReportGroupReviewStatus.RESOLVED),
                reportGroupRepository.countByReviewStatusAndDeletedAtIsNull(ReportGroupReviewStatus.REJECTED),
                reportGroupRepository.countBySanctionStatusAndDeletedAtIsNull(ReportGroupSanctionStatus.NONE),
                reportGroupRepository.countBySanctionStatusAndDeletedAtIsNull(ReportGroupSanctionStatus.AUTO_BLINDED),
                reportGroupRepository.countBySanctionStatusAndDeletedAtIsNull(ReportGroupSanctionStatus.MANUAL_BLINDED)
        );
    }

    public record ReportGroupSummary(
            long totalCount, // 전체 신고 그룹 수
            long pendingCount, // 대기 중인 신고 그룹 수
            long resolvedCount, // 처리 완료된 신고 그룹 수
            long rejectedCount, // 반려된 신고 그룹 수
            long noneSanctionCount, // 제재 없는 상태 수
            long autoBlindedCount, // 자동 블라인드 상태 수
            long manualBlindedCount // 수동 블라인드 상태 수
    ) {
    }
}
