package com.ssambbong.gymjjak.report.application.usecase;

import com.ssambbong.gymjjak.report.application.query.AdminReportListQuery;
import com.ssambbong.gymjjak.report.application.query.AdminReportListResult;

public interface ReportQueryUseCase {

    AdminReportListResult findReportGroups(AdminReportListQuery query);
}
