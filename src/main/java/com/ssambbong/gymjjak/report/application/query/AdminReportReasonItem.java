package com.ssambbong.gymjjak.report.application.query;

import com.ssambbong.gymjjak.report.domain.model.ReportReasonType;
import com.ssambbong.gymjjak.report.domain.model.ReportStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public record AdminReportReasonItem(
        Long reportId,
        String reporterUsername,
        ReportReasonType reason,
        String detail,
        LocalDateTime reportedAt,
        ReportStatus status
) {

}
