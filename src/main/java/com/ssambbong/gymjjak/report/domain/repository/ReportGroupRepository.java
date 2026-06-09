package com.ssambbong.gymjjak.report.domain.repository;

import com.ssambbong.gymjjak.report.application.query.AdminReportDetailResult;
import com.ssambbong.gymjjak.report.application.query.AdminReportListQuery;
import com.ssambbong.gymjjak.report.application.query.AdminReportListResult;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportGroupRepository {

    ReportGroup save(ReportGroup reportGroup);

    Optional<ReportGroup> findById(Long reportGroupId);

    AdminReportListResult findAdminReportList(AdminReportListQuery query);

    AdminReportDetailResult findReportDetail(Long reportGroupId);

    // 타겟 타입, 타겟 아이디로 피신고 게시글 조회
    Optional<ReportGroup> findByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);

    boolean existsByReportNumber(String reportNumber);

    // 신고그룹 검토 상태별 개수 조회
    long countByReviewStatusAndDeletedAtIsNull(ReportGroupReviewStatus reportGroupReviewStatus);

    // 신고그룹 제재 상태별 개수 조회
    long countBySanctionStatusAndDeletedAtIsNull(ReportGroupSanctionStatus reportGroupSanctionStatus);

    // 전체 조회
    long countAllByDeletedAtIsNull();

    // 수동 제재 + 처리 완료 + 수정일이 threshold 보다 오래된 신고 그룹 조회
    List<Long> findManualBlindedResolvedHardDeleteCandidateIds(
            LocalDateTime threshold,
            int batchSize
    );

    // 신고그룹 하드 삭제 하기
    int hardDeleteByIds(List<Long> reportGroupIds);

}
