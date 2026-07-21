package com.ssambbong.gymjjak.report.presentation.api.response;

import com.ssambbong.gymjjak.report.application.query.AdminReportSnapshotResult;
import lombok.Builder;

@Builder
public record AdminReportSnapshotResponse(
        Long reportGroupId,
        String targetType,
        Long targetId,
        String title,
        String content,
        String fileUrl
) {
    public static AdminReportSnapshotResponse from(AdminReportSnapshotResult result) {
        return AdminReportSnapshotResponse.builder()
                .reportGroupId(result.reportGroupId())
                .targetType(result.targetType().getDescription())
                .targetId(result.targetId())
                .title(result.title())
                .content(result.content())
                .fileUrl(result.fileUrl())
                .build();
    }
}
