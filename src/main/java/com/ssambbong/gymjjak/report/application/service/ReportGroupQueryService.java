package com.ssambbong.gymjjak.report.application.service;

import com.ssambbong.gymjjak.report.application.query.AdminReportDetailResult;
import com.ssambbong.gymjjak.report.application.query.AdminReportListQuery;
import com.ssambbong.gymjjak.report.application.query.AdminReportListResult;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupQueryUseCase;
import com.ssambbong.gymjjak.report.domain.repository.ReportGroupRepository;
import com.ssambbong.gymjjak.report.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportGroupQueryService implements ReportGroupQueryUseCase {

    private final ReportGroupRepository reportGroupRepository;
    private final ReportRepository reportRepository;

    @Override
    public AdminReportListResult findReportGroups(AdminReportListQuery query) {

        return reportGroupRepository.findAdminReportList(query);
    }

    @Override
    public AdminReportDetailResult findReportDetail(Long reportGroupId) {

        return reportGroupRepository.findReportDetail(reportGroupId);
    }
}
