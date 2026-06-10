package com.ssambbong.gymjjak.report.application.service;

import com.ssambbong.gymjjak.report.application.command.ApproveReportCommand;
import com.ssambbong.gymjjak.report.application.command.ManualBlindReportGroupCommand;
import com.ssambbong.gymjjak.report.application.command.RejectReportCommand;
import com.ssambbong.gymjjak.report.application.metrics.ReportGroupTimed;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionTargetPort;
import com.ssambbong.gymjjak.report.application.port.UserProfileView;
import com.ssambbong.gymjjak.report.application.port.UserQueryPort;
import com.ssambbong.gymjjak.report.application.query.AdminReportReasonItem;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupCommandUseCase;
import com.ssambbong.gymjjak.report.domain.exception.ReportGroupNotFoundException;
import com.ssambbong.gymjjak.report.domain.exception.ReportGroupSoftDeleteFailedException;
import com.ssambbong.gymjjak.report.domain.exception.ReportNotFoundException;
import com.ssambbong.gymjjak.report.domain.model.Report;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import com.ssambbong.gymjjak.report.infrastructure.metrics.ReportGroupMetric;
import com.ssambbong.gymjjak.report.infrastructure.metrics.ReportMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReportGroupCommandService implements ReportGroupCommandUseCase {

    private final ReportRepository reportRepository;
    private final ReportGroupRepository reportGroupRepository;
    private final ReportSanctionTargetPort  reportSanctionTargetPort;
    private final UserQueryPort userQueryPort;

    private final ReportGroupMetric reportGroupMetric;
    private final ReportMetric reportMetric;

    @ReportGroupTimed(action = "approve")
    @Override
    public AdminReportReasonItem approveReport(ApproveReportCommand command) {

        log.info("[ReportCommandService] 관리자 개별 신고 승인 처리 시작 - reportGroupId: {}, reportId: {}, adminId: {}",
                command.reportGroupId(), command.reportId(), command.adminId());

        // 단건 신고 내역 엔티티 조회
        Report report = getReport(command.reportId());

        // 도메인 - 신고그룹의 소속 개별 신고인지 검증
        report.validateReportGroupId(command.reportGroupId());

        // 도메인 - 단건 신고 상태 pending -> approve
        report.approve(command.adminId(), LocalDateTime.now());

        log.debug("[ReportCommandService] 개별 신고 상태 APPROVED 변경 완료 - reportId: {}", report.getReportId());

        // 변경 신고 상태 영속성 계층, java repo에 저장
        Report savedReport = reportRepository.save(report);

        // 상위 신고 그룹 가져오기
        ReportGroup reportGroup = getReportGroup(command.reportGroupId());

        // Metric 변경 전 검토 상태 저장
        ReportGroupReviewStatus previousReviewStatus = reportGroup.getReviewStatus();

        // 하위 모든 개별 신고 리스트 조회 후 전달
        List<Report> reports = reportRepository.findAllByReportGroupId(command.reportGroupId());

        // 하위 개별 신고 상태 기반 신고 그룹 최종 상태 재개산
        // pending 수 확인 후, approve가 있는지 판단하여 신고그룹 감터 싱태 대기/해결/반려 결정
        reportGroup.recalculateReviewStatus(reports);
        // 유효 승인 건수가 5개 이상인지 체크하여 AUTO_BLINDED 혹은 NONE 결정
        reportGroup.syncAutoSanctionStatus();
        // 처리 관리자 pk값 기록
        reportGroup.markProcessedBy(command.adminId());

        // 최종 재계산 신고 그룹 데이터 저장
        reportGroupRepository.save(reportGroup);

        // 개별 신고 승인 성공 Metric 저장
        reportMetric.countApprovedReport();

        // 신고 그룹 전체 처리가 끝나면 해결 Metric 저장
        if (previousReviewStatus == ReportGroupReviewStatus.PENDING
                && reportGroup.getReviewStatus() != ReportGroupReviewStatus.PENDING) {
            reportGroupMetric.countCompletedReportGroup();
        }

        log.info("[ReportCommandService] 관리자 개별 신고 승인 및 그룹 상태 재계산 완료 - reportGroupId: {}, 최종 리뷰 상태: {}, 최종 제재 상태: {}",
                reportGroup.getReportGroupId(), reportGroup.getReviewStatus(), reportGroup.getSanctionStatus());

        // 화면 뿌려줄 dto 형태로 변경하여 리턴
        return toReviewReportResult(savedReport);
    }

    @ReportGroupTimed(action = "reject")
    @Override
    public AdminReportReasonItem rejectReport(RejectReportCommand command) {

        log.info("[ReportCommandService] 관리자 개별 신고 반려 처리 시작 - reportGroupId: {}, reportId: {}, adminId: {}",
                command.reportGroupId(), command.reportId(), command.adminId());
        // 신고 조회
        Report report = getReport(command.reportId());
        // 신고 그룹 소속 신고 여부 검증
        report.validateReportGroupId(command.reportGroupId());

        report.reject(command.adminId(), LocalDateTime.now());
        Report savedReport = reportRepository.save(report);

        ReportGroup reportGroup = getReportGroup(command.reportGroupId());
        // Metric 카운터용 이전 검토 상태 저장
        ReportGroupReviewStatus previousReviewStatus = reportGroup.getReviewStatus();

        ReportGroupSanctionStatus previousSanctionStatus = reportGroup.getSanctionStatus();

        // 반려 시 유효 신고 수 감소
        reportGroup.decreaseEffectiveReportCount();

        List<Report> reports = reportRepository.findAllByReportGroupId(command.reportGroupId());
        // 신고 그룹 제재 상태 동기화
        reportGroup.recalculateReviewStatus(reports);
        reportGroup.syncAutoSanctionStatus();
        reportGroup.markProcessedBy(command.adminId());

        reportGroupRepository.save(reportGroup);

        if (previousSanctionStatus == ReportGroupSanctionStatus.AUTO_BLINDED
                && reportGroup.getSanctionStatus() == ReportGroupSanctionStatus.NONE) {
            reportSanctionTargetPort.applySanction(
                    reportGroup.getTargetType(),
                    reportGroup.getTargetId(),
                    ReportSanctionAction.RELEASE_AUTO_BLIND
            );
        }

        log.info("[ReportCommandService] 관리자 개별 신고 반려 및 그룹 상태 동기화 완료 - reportGroupId: {}, 최종 리뷰 상태: {}, 최종 제재 상태: {}",
                reportGroup.getReportGroupId(), reportGroup.getReviewStatus(), reportGroup.getSanctionStatus());

        // 개별 신고 반려 성공 카운트
        reportMetric.countRejectedReport();

        // 신고 그룹 처리 완료 시, 해결 메트릭 저장
        if (previousReviewStatus == ReportGroupReviewStatus.PENDING
                && reportGroup.getReviewStatus() != ReportGroupReviewStatus.PENDING) {
            reportGroupMetric.countCompletedReportGroup();
        }

        return toReviewReportResult(savedReport);
    }

    @Override
    public void manuallyBlindReportGroup(ManualBlindReportGroupCommand command) {
        log.info("event=reportGroup_manualBlind_start reportGroupId= {}", command.reportGroupId());

        LocalDateTime now = LocalDateTime.now();

        ReportGroup reportGroup = getReportGroup(command.reportGroupId());

        reportGroup.manuallyBlind(command.adminId());

        ReportGroup savedReportGroup = reportGroupRepository.save(reportGroup);

        reportSanctionTargetPort.applySanction(
                savedReportGroup.getTargetType(),
                savedReportGroup.getTargetId(),
                ReportSanctionAction.APPLY_MANUAL_BLIND
        );

        int softDeleted = reportGroupRepository.softDeleteResolvedManualBlindedById(
                savedReportGroup.getReportGroupId(),
                now
        );

        if (softDeleted != 1) {
            throw new ReportGroupSoftDeleteFailedException(
                    savedReportGroup.getReportGroupId()
            );
        }

        log.info(
                "event=reportGroup_manualBlind_completed reportGroupId: {}, targetType: {}, targetId: {}, adminId: {}",
                savedReportGroup.getReportGroupId(),
                savedReportGroup.getTargetType(),
                savedReportGroup.getTargetId(),
                command.adminId()
        );
    }

    private Report getReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
    }

    private ReportGroup getReportGroup(Long reportGroupId) {
        return reportGroupRepository.findById(reportGroupId)
                .orElseThrow(() -> new ReportGroupNotFoundException(reportGroupId));
    }

    private AdminReportReasonItem toReviewReportResult(Report report) {
        String reporterUsername = userQueryPort.findUserProfile(report.getReporterId())
                .map(UserProfileView::username)
                .orElse("알 수 없음");

        return new AdminReportReasonItem(
                report.getReporterId(),
                reporterUsername,
                report.getReason(),
                report.getDetail(),
                report.getCreatedAt(),
                report.getStatus()
        );
    }
}
