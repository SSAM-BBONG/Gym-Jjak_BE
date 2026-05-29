package com.ssambbong.gymjjak.report.application.command;

public record RejectReportCommand(
        Long reportGroupId,
        Long reportId,
        Long adminId
) {
}
