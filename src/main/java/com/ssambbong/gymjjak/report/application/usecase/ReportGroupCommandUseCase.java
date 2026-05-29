package com.ssambbong.gymjjak.report.application.usecase;

import com.ssambbong.gymjjak.report.application.command.ApproveReportCommand;
import com.ssambbong.gymjjak.report.application.command.RejectReportCommand;

public interface ReportGroupCommandUseCase {

    void approveReport(ApproveReportCommand command);
    void rejectReport(RejectReportCommand command);
}
