package com.ssambbong.gymjjak.report.application.service;

import com.ssambbong.gymjjak.report.application.command.ApproveReportCommand;
import com.ssambbong.gymjjak.report.application.command.RejectReportCommand;
import com.ssambbong.gymjjak.report.application.usecase.ReportCommandUseCase;
import com.ssambbong.gymjjak.report.domain.exception.InvalidReportGroupRelationException;
import com.ssambbong.gymjjak.report.domain.exception.ReportNotFoundException;
import com.ssambbong.gymjjak.report.domain.model.Report;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReportCommandService implements ReportCommandUseCase {

    private final ReportRepository reportRepository;

    @Override
    public void approveReport(ApproveReportCommand command) {
        Report report = getReport(command.reportId());

        report.validateReportGroupId(command.reportGroupId());

        report.approve(command.adminId(), LocalDateTime.now());

        reportRepository.save(report);
    }

    @Override
    public void rejectReport(RejectReportCommand command) {
        Report report = getReport(command.reportId());

        report.validateReportGroupId(command.reportGroupId());

        report.reject(command.adminId(), LocalDateTime.now());

        reportRepository.save(report);
    }

    private Report getReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
    }
}
