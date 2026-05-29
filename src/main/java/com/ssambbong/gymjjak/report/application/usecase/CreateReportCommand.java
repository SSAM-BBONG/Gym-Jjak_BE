package com.ssambbong.gymjjak.report.application.usecase;

import com.ssambbong.gymjjak.report.domain.model.ReportReasonType;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;

public record CreateReportCommand(
        Long reporterId,
        Long targetId,
        ReportTargetType targetType,
        ReportReasonType reason,
        String detail
) {
}
