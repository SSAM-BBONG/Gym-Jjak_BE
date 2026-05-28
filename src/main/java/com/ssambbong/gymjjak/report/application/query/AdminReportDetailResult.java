package com.ssambbong.gymjjak.report.application.query;

import com.ssambbong.gymjjak.report.domain.model.ReportGroupStatus;

import java.util.List;

public record AdminReportDetailResult(
        Long reportGroupId,
        ReportGroupStatus status,
        List<AdminReportReasonItem> reports
) {
}
