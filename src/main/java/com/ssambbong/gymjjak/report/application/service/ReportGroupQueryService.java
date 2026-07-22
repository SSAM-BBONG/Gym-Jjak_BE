package com.ssambbong.gymjjak.report.application.service;

import com.ssambbong.gymjjak.report.application.query.AdminReportDetailResult;
import com.ssambbong.gymjjak.report.application.query.AdminReportListQuery;
import com.ssambbong.gymjjak.report.application.query.AdminReportListResult;
import com.ssambbong.gymjjak.report.application.query.AdminReportSnapshotResult;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupQueryUseCase;
import com.ssambbong.gymjjak.report.domain.exception.ReportGroupNotFoundException;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import com.ssambbong.gymjjak.report.infrastructure.metrics.ReportGroupMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReportGroupQueryService implements ReportGroupQueryUseCase {

    private final ReportGroupRepository reportGroupRepository;
    private final ReportRepository reportRepository;

    @Override
    public AdminReportListResult findReportGroups(AdminReportListQuery query) {

        log.info("[ReportQuery] 관리자 신고 목록 조회 요청 시작 - targetType: {}", query.targetType());

        log.debug("[ReportQuery] 세부 페이징 조건 - page: {}, size: {}", query.page(), query.size());

        // 아웃바운드포트 호출하여 DB 조회!
        AdminReportListResult result = reportGroupRepository.findAdminReportList(query);

        log.info("[ReportQuery] 관리자 신고 목록 조회 성공 - 반환 아이템 개수: {}, 총 페이지 개수: {}",
                result.items().size(), result.totalPages());
        return result;
    }

    @Override
    public AdminReportDetailResult findReportDetail(Long reportGroupId) {

        log.info("[ReportQueryService] 관리자 신고 상세 조회 요청 시작 - reportGroupId: {}", reportGroupId);

        log.debug("[ReportQueryService] ReportGroupRepository 단건 상세 조회 쿼리 요청 시작");
        AdminReportDetailResult result = reportGroupRepository.findReportDetail(reportGroupId);

        // TODO : 여기에 개별 사유 개수 추가해도 괜찮을듯?
        log.info("[ReportQueryService] 관리자 신고 상세 조회 완료 - 신고 번호: {}",
                result.reportGroupId());

        return result;
    }
    @Override
    public AdminReportSnapshotResult findReportSnapshot(Long reportGroupId) {
        // 활성화된 신고그룹 조회
        ReportGroup reportGroup = reportGroupRepository.findActiveById(reportGroupId)
                .orElseThrow(() -> new ReportGroupNotFoundException(reportGroupId));

        return new AdminReportSnapshotResult(
                reportGroup.getReportGroupId(),
                reportGroup.getTargetType(),
                reportGroup.getTargetId(),
                reportGroup.getSnapshotTitle(),
                reportGroup.getSnapshotContent(),
                reportGroup.getSnapshotFileUrl()
        );
    }
}
