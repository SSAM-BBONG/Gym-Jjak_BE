package com.ssambbong.gymjjak.report.application.query;

import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;

public record AdminReportSnapshotResult(
        Long reportGroupId,
        ReportTargetType targetType,
        Long targetId,
        String title,
        String content,
        String fileUrl
) {
}
