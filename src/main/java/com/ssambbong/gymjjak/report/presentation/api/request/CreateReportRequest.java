package com.ssambbong.gymjjak.report.presentation.api.request;

import com.ssambbong.gymjjak.report.domain.model.ReportReasonType;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;

public record CreateReportRequest(
        Long targetId,
        ReportTargetType targetType,
        ReportReasonType reason,
        String detail
) {
}
