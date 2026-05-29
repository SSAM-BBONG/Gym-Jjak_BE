package com.ssambbong.gymjjak.report.application.command;

public record ApproveReportCommand(
        Long reportGroupId,
        Long reportId,
        Long adminId
) {
}
