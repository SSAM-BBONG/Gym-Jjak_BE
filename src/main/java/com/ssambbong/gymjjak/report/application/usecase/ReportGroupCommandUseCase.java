package com.ssambbong.gymjjak.report.application.usecase;

import com.ssambbong.gymjjak.report.application.command.ApproveReportCommand;
import com.ssambbong.gymjjak.report.application.command.RejectReportCommand;
import com.ssambbong.gymjjak.report.application.query.AdminReportReasonItem;

public interface ReportGroupCommandUseCase {

    AdminReportReasonItem approveReport(ApproveReportCommand command);
    AdminReportReasonItem rejectReport(RejectReportCommand command);
}
