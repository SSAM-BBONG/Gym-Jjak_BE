package com.ssambbong.gymjjak.dashboard.admin.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminContentStatisticsResult;
import lombok.Builder;

@Builder
public record AdminContentStatisticsResponse(
        long activePtCourseCount,
        long blindedPtCourseCount,
        long pendingReportGroupCount
) {

    public static AdminContentStatisticsResponse from(
            AdminContentStatisticsResult result
    ) {
        return AdminContentStatisticsResponse.builder()
                .activePtCourseCount(result.activePtCourseCount())
                .blindedPtCourseCount(result.blindedPtCourseCount())
                .pendingReportGroupCount(result.pendingReportGroupCount())
                .build();
    }
}
