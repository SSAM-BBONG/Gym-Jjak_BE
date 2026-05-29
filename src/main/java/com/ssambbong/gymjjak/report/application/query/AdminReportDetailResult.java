package com.ssambbong.gymjjak.report.application.query;

import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;

import java.util.List;

public record AdminReportDetailResult(
        Long reportGroupId,
        ReportGroupReviewStatus status,
        List<AdminReportReasonItem> reports
) {
}
