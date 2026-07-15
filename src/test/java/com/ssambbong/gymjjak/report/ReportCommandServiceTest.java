package com.ssambbong.gymjjak.report;

import com.ssambbong.gymjjak.report.application.command.ApproveReportCommand;
import com.ssambbong.gymjjak.report.application.command.RejectReportCommand;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionTargetPort;
import com.ssambbong.gymjjak.report.application.port.user.UserQueryPort;
import com.ssambbong.gymjjak.report.application.service.ReportGroupCommandService;
import com.ssambbong.gymjjak.report.domain.model.*;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import com.ssambbong.gymjjak.report.infrastructure.metrics.ReportGroupMetric;
import com.ssambbong.gymjjak.report.infrastructure.metrics.ReportMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReportCommandServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportGroupRepository reportGroupRepository;

    @Mock
    private ReportSanctionTargetPort reportSanctionTargetPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private ReportGroupMetric reportGroupMetric;

    @Mock
    private ReportMetric reportMetric;

    @InjectMocks
    private ReportGroupCommandService reportCommandService;

    private ApproveReportCommand command;

    @BeforeEach
    void setUp() {
        command = new ApproveReportCommand(1L, 100L, 999L);
    }

    @Test
    @DisplayName("신고 승인 시 개별 신고 상태가 APPROVED로 변경된다")
    void approveReport_changesReportStatusToApproved() {
        // given
        Report report = pendingReport(100L, 1L);

        ReportGroup reportGroup = reportGroup(
                1L,
                3,
                3,
                ReportGroupReviewStatus.PENDING,
                ReportGroupSanctionStatus.NONE,
                ReportTargetType.POST
        );

        List<Report> reports = List.of(
                approvedReport(101L, 1L),
                report
        );

        given(reportRepository.findById(100L)).willReturn(Optional.of(report));
        given(reportRepository.save(any(Report.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(reportGroupRepository.findById(1L)).willReturn(Optional.of(reportGroup));
        given(reportRepository.findAllByReportGroupId(1L)).willReturn(reports);
        given(reportGroupRepository.save(any(ReportGroup.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        reportCommandService.approveReport(command);

        // then
        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());

        Report savedReport = reportCaptor.getValue();
        assertThat(savedReport.getStatus()).isEqualTo(ReportStatus.APPROVED);
        assertThat(savedReport.getProcessedBy()).isEqualTo(999L);
        assertThat(savedReport.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("마지막 PENDING 신고를 승인하면 그룹 검토 상태가 RESOLVED가 된다")
    void approveReport_lastPendingChangesGroupReviewStatusToResolved() {
        // given
        Report report = pendingReport(100L, 1L);

        ReportGroup reportGroup = reportGroup(
                1L,
                3,
                3,
                ReportGroupReviewStatus.PENDING,
                ReportGroupSanctionStatus.NONE,
                ReportTargetType.POST
        );

        List<Report> reports = List.of(
                approvedReport(101L, 1L),
                approvedReport(100L, 1L)
        );

        given(reportRepository.findById(100L)).willReturn(Optional.of(report));
        given(reportRepository.save(any(Report.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(reportGroupRepository.findById(1L)).willReturn(Optional.of(reportGroup));
        given(reportRepository.findAllByReportGroupId(1L)).willReturn(reports);
        given(reportGroupRepository.save(any(ReportGroup.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        reportCommandService.approveReport(command);

        // then
        ArgumentCaptor<ReportGroup> groupCaptor = ArgumentCaptor.forClass(ReportGroup.class);
        verify(reportGroupRepository).save(groupCaptor.capture());

        ReportGroup savedGroup = groupCaptor.getValue();
        assertThat(savedGroup.getReviewStatus()).isEqualTo(ReportGroupReviewStatus.RESOLVED);
        assertThat(savedGroup.getEffectiveReportCount()).isEqualTo(3);
        assertThat(savedGroup.getProcessedBy()).isEqualTo(999L);
    }

    @Test
    @DisplayName("MANUAL_BLINDED 상태에서는 신고 승인 후에도 제재 상태가 유지된다")
    void approveReport_keepsManualBlindedSanctionStatus() {
        // given
        Report report = pendingReport(100L, 1L);

        ReportGroup reportGroup = reportGroup(
                1L,
                5,
                5,
                ReportGroupReviewStatus.PENDING,
                ReportGroupSanctionStatus.MANUAL_BLINDED,
                ReportTargetType.POST
        );

        List<Report> reports = List.of(
                approvedReport(101L, 1L),
                approvedReport(100L, 1L)
        );

        given(reportRepository.findById(100L)).willReturn(Optional.of(report));
        given(reportRepository.save(any(Report.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(reportGroupRepository.findById(1L)).willReturn(Optional.of(reportGroup));
        given(reportRepository.findAllByReportGroupId(1L)).willReturn(reports);
        given(reportGroupRepository.save(any(ReportGroup.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        reportCommandService.approveReport(command);

        // then
        ArgumentCaptor<ReportGroup> groupCaptor = ArgumentCaptor.forClass(ReportGroup.class);
        verify(reportGroupRepository).save(groupCaptor.capture());

        ReportGroup savedGroup = groupCaptor.getValue();
        assertThat(savedGroup.getSanctionStatus()).isEqualTo(ReportGroupSanctionStatus.MANUAL_BLINDED);
    }

    private Report pendingReport(Long reportId, Long reportGroupId) {
        return Report.reconstruct(
                reportId,
                reportGroupId,
                10L,
                ReportReasonType.ABUSE,
                "욕설 신고",
                ReportStatus.PENDING,
                null,
                null,
                LocalDateTime.of(2026, 5, 29, 10, 0)
        );
    }

    private Report approvedReport(Long reportId, Long reportGroupId) {
        return Report.reconstruct(
                reportId,
                reportGroupId,
                10L,
                ReportReasonType.ABUSE,
                "욕설 신고",
                ReportStatus.APPROVED,
                999L,
                LocalDateTime.of(2026, 5, 29, 11, 0),
                LocalDateTime.of(2026, 5, 29, 10, 0)
        );
    }

    private ReportGroup reportGroup(
            Long reportGroupId,
            int totalReportCount,
            int effectiveReportCount,
            ReportGroupReviewStatus reviewStatus,
            ReportGroupSanctionStatus sanctionStatus,
            ReportTargetType targetType
    ) {
        return ReportGroup.reconstruct(
                reportGroupId,
                "RPT-202605-0001",
                targetType,
                1L,
                20L,
                "신고 대상 제목",
                "신고 대상 내용",
                null,
                totalReportCount,
                effectiveReportCount,
                reviewStatus,
                sanctionStatus,
                null,
                LocalDateTime.of(2026, 5, 29, 9, 0),
                LocalDateTime.of(2026, 5, 29, 9, 0),
                null
        );
    }

    @Test
    @DisplayName("신고 반려 시 개별 신고 상태가 REJECTED로 변경된다")
    void rejectReport_changesReportStatusToRejected() {
        // given
        RejectReportCommand command = new RejectReportCommand(1L, 100L, 999L);

        Report report = pendingReport(100L, 1L);

        ReportGroup reportGroup = reportGroup(
                1L,
                3,
                3,
                ReportGroupReviewStatus.PENDING,
                ReportGroupSanctionStatus.NONE,
                ReportTargetType.POST
        );

        List<Report> reports = List.of(
                approvedReport(101L, 1L),
                rejectedReport(100L, 1L)
        );

        given(reportRepository.findById(100L)).willReturn(Optional.of(report));
        given(reportRepository.save(any(Report.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(reportGroupRepository.findById(1L)).willReturn(Optional.of(reportGroup));
        given(reportRepository.findAllByReportGroupId(1L)).willReturn(reports);
        given(reportGroupRepository.save(any(ReportGroup.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        reportCommandService.rejectReport(command);

        // then
        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());

        Report savedReport = reportCaptor.getValue();
        assertThat(savedReport.getStatus()).isEqualTo(ReportStatus.REJECTED);
        assertThat(savedReport.getProcessedBy()).isEqualTo(999L);
        assertThat(savedReport.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("신고 반려 시 effectiveReportCount가 1 감소한다")
    void rejectReport_decreasesEffectiveReportCount() {
        // given
        RejectReportCommand command = new RejectReportCommand(1L, 100L, 999L);

        Report report = pendingReport(100L, 1L);

        ReportGroup reportGroup = reportGroup(
                1L,
                3,
                3,
                ReportGroupReviewStatus.PENDING,
                ReportGroupSanctionStatus.NONE,
                ReportTargetType.POST
        );

        List<Report> reports = List.of(
                approvedReport(101L, 1L),
                rejectedReport(100L, 1L)
        );

        given(reportRepository.findById(100L)).willReturn(Optional.of(report));
        given(reportRepository.save(any(Report.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(reportGroupRepository.findById(1L)).willReturn(Optional.of(reportGroup));
        given(reportRepository.findAllByReportGroupId(1L)).willReturn(reports);
        given(reportGroupRepository.save(any(ReportGroup.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        reportCommandService.rejectReport(command);

        // then
        ArgumentCaptor<ReportGroup> groupCaptor = ArgumentCaptor.forClass(ReportGroup.class);
        verify(reportGroupRepository).save(groupCaptor.capture());

        ReportGroup savedGroup = groupCaptor.getValue();
        assertThat(savedGroup.getEffectiveReportCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("마지막 PENDING 신고를 반려하고 승인된 신고가 있으면 그룹 검토 상태가 RESOLVED가 된다")
    void rejectReport_lastPendingWithApprovedChangesReviewStatusToResolved() {
        // given
        RejectReportCommand command = new RejectReportCommand(1L, 100L, 999L);

        Report report = pendingReport(100L, 1L);

        ReportGroup reportGroup = reportGroup(
                1L,
                3,
                3,
                ReportGroupReviewStatus.PENDING,
                ReportGroupSanctionStatus.NONE,
                ReportTargetType.POST
        );

        List<Report> reports = List.of(
                approvedReport(101L, 1L),
                rejectedReport(100L, 1L)
        );

        given(reportRepository.findById(100L)).willReturn(Optional.of(report));
        given(reportRepository.save(any(Report.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(reportGroupRepository.findById(1L)).willReturn(Optional.of(reportGroup));
        given(reportRepository.findAllByReportGroupId(1L)).willReturn(reports);
        given(reportGroupRepository.save(any(ReportGroup.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        reportCommandService.rejectReport(command);

        // then
        ArgumentCaptor<ReportGroup> groupCaptor = ArgumentCaptor.forClass(ReportGroup.class);
        verify(reportGroupRepository).save(groupCaptor.capture());

        ReportGroup savedGroup = groupCaptor.getValue();
        assertThat(savedGroup.getReviewStatus()).isEqualTo(ReportGroupReviewStatus.RESOLVED);
    }

    @Test
    @DisplayName("AUTO_BLINDED 상태에서 반려 후 유효 신고 수가 5 미만이 되면 제재 상태가 NONE으로 해제된다")
    void rejectReport_releasesAutoBlindedWhenEffectiveCountFallsBelowThreshold() {
        // given
        RejectReportCommand command = new RejectReportCommand(1L, 100L, 999L);

        Report report = pendingReport(100L, 1L);

        ReportGroup reportGroup = reportGroup(
                1L,
                5,
                5,
                ReportGroupReviewStatus.PENDING,
                ReportGroupSanctionStatus.AUTO_BLINDED,
                ReportTargetType.POST
        );

        List<Report> reports = List.of(
                approvedReport(101L, 1L),
                approvedReport(102L, 1L),
                approvedReport(103L, 1L),
                approvedReport(104L, 1L),
                rejectedReport(100L, 1L)
        );

        given(reportRepository.findById(100L)).willReturn(Optional.of(report));
        given(reportRepository.save(any(Report.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(reportGroupRepository.findById(1L)).willReturn(Optional.of(reportGroup));
        given(reportRepository.findAllByReportGroupId(1L)).willReturn(reports);
        given(reportGroupRepository.save(any(ReportGroup.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        reportCommandService.rejectReport(command);

        // then
        ArgumentCaptor<ReportGroup> groupCaptor = ArgumentCaptor.forClass(ReportGroup.class);
        verify(reportGroupRepository).save(groupCaptor.capture());

        ReportGroup savedGroup = groupCaptor.getValue();
        assertThat(savedGroup.getEffectiveReportCount()).isEqualTo(4);
        assertThat(savedGroup.getSanctionStatus()).isEqualTo(ReportGroupSanctionStatus.NONE);
    }

    @Test
    @DisplayName("그룹의 모든 신고가 반려되면 그룹 검토 상태가 REJECTED가 된다")
    void rejectReport_allRejectedChangesReviewStatusToRejected() {
        // given
        RejectReportCommand command = new RejectReportCommand(1L, 100L, 999L);

        Report report = pendingReport(100L, 1L);

        ReportGroup reportGroup = reportGroup(
                1L,
                2,
                2,
                ReportGroupReviewStatus.PENDING,
                ReportGroupSanctionStatus.NONE,
                ReportTargetType.POST
        );

        List<Report> reports = List.of(
                rejectedReport(101L, 1L),
                rejectedReport(100L, 1L)
        );

        given(reportRepository.findById(100L)).willReturn(Optional.of(report));
        given(reportRepository.save(any(Report.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(reportGroupRepository.findById(1L)).willReturn(Optional.of(reportGroup));
        given(reportRepository.findAllByReportGroupId(1L)).willReturn(reports);
        given(reportGroupRepository.save(any(ReportGroup.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        reportCommandService.rejectReport(command);

        // then
        ArgumentCaptor<ReportGroup> groupCaptor = ArgumentCaptor.forClass(ReportGroup.class);
        verify(reportGroupRepository).save(groupCaptor.capture());

        ReportGroup savedGroup = groupCaptor.getValue();
        assertThat(savedGroup.getReviewStatus()).isEqualTo(ReportGroupReviewStatus.REJECTED);
        assertThat(savedGroup.getEffectiveReportCount()).isEqualTo(1);
    }

    private Report rejectedReport(Long reportId, Long reportGroupId) {
        return Report.reconstruct(
                reportId,
                reportGroupId,
                10L,
                ReportReasonType.ABUSE,
                "욕설 신고",
                ReportStatus.REJECTED,
                999L,
                LocalDateTime.of(2026, 5, 29, 11, 30),
                LocalDateTime.of(2026, 5, 29, 10, 0)
        );
    }
}
