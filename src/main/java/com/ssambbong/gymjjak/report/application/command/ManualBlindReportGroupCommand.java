package com.ssambbong.gymjjak.report.application.command;

import com.ssambbong.gymjjak.report.domain.exception.ReportGroupNotFoundException;

public record ManualBlindReportGroupCommand(
        Long reportGroupId,
        Long adminId
) {
}
