package com.ssambbong.gymjjak.dashboard.admin.application.query;

import lombok.Builder;

@Builder
public record AdminContentStatisticsResult(
        long activePtCourseCount,
        long blindedPtCourseCount,
        long pendingReportGroupCount
) {
}
