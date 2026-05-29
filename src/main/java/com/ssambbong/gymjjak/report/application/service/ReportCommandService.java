package com.ssambbong.gymjjak.report.application.service;

import com.ssambbong.gymjjak.report.application.command.ApproveReportCommand;
import com.ssambbong.gymjjak.report.application.command.RejectReportCommand;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionTargetPort;
import com.ssambbong.gymjjak.report.application.usecase.ReportCommandUseCase;
import com.ssambbong.gymjjak.report.domain.exception.ReportGroupNotFoundException;
import com.ssambbong.gymjjak.report.domain.exception.ReportNotFoundException;
import com.ssambbong.gymjjak.report.domain.model.Report;
import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
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
public class ReportCommandService implements ReportCommandUseCase {

    private final ReportRepository reportRepository;
    private final ReportGroupRepository reportGroupRepository;
    private final ReportSanctionTargetPort  reportSanctionTargetPort;

    @Override
    public void approveReport(ApproveReportCommand command) {
        Report report = getReport(command.reportId());

        report.validateReportGroupId(command.reportGroupId());

        report.approve(command.adminId(), LocalDateTime.now());
        reportRepository.save(report);

        ReportGroup reportGroup = getReportGroup(command.reportGroupId());
        List<Report> reports = reportRepository.findAllByReportGroupId(command.reportGroupId());


        reportGroup.recalculateReviewStatus(reports);
        reportGroup.syncAutoSanctionStatus();
        reportGroup.markProcessedBy(command.adminId());

        reportGroupRepository.save(reportGroup);
    }

    @Override
    public void rejectReport(RejectReportCommand command) {
        Report report = getReport(command.reportId());

        report.validateReportGroupId(command.reportGroupId());

        report.reject(command.adminId(), LocalDateTime.now());
        reportRepository.save(report);

        ReportGroup reportGroup = getReportGroup(command.reportGroupId());

        ReportGroupSanctionStatus previousSanctionStatus = reportGroup.getSanctionStatus();

        reportGroup.decreaseEffectiveReportCount();

        List<Report> reports = reportRepository.findAllByReportGroupId(command.reportGroupId());

        reportGroup.recalculateReviewStatus(reports);
        reportGroup.syncAutoSanctionStatus();
        reportGroup.markProcessedBy(command.adminId());

        reportGroupRepository.save(reportGroup);

        if (previousSanctionStatus == ReportGroupSanctionStatus.AUTO_BLINDED
                && reportGroup.getSanctionStatus() == ReportGroupSanctionStatus.NONE) {
            reportSanctionTargetPort.changeAutoBlind(
                    reportGroup.getTargetType(),
                    reportGroup.getTargetId(),
                    ReportSanctionAction.RELEASE_AUTO_BLIND
            );
        }
    }

    private Report getReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
    }

    private ReportGroup getReportGroup(Long reportGroupId) {
        return reportGroupRepository.findById(reportGroupId)
                .orElseThrow(() -> new ReportGroupNotFoundException(reportGroupId));
    }
}
