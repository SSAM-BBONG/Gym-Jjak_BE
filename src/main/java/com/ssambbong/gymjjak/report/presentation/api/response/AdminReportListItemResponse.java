package com.ssambbong.gymjjak.report.presentation.api.response;

import com.ssambbong.gymjjak.report.application.query.AdminReportListItem;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AdminReportListItemResponse(
        String reportNumber,
        String targetType,
        Long targetId,
        String targetDisplayText,
        String targetOwnerUsername,
        LocalDateTime reportedAt,
        int reportCount,
        String status,
        String navigationType
) {
    public static AdminReportListItemResponse from(AdminReportListItem item) {
        return AdminReportListItemResponse.builder()
                .reportNumber(item.reportNumber())
                .targetType(item.targetType().getDescription())
                .targetId(item.targetId())
                .targetDisplayText(item.targetDisplayText())
                .targetOwnerUsername(item.targetOwnerUsername())
                .reportedAt(item.reportedAt())
                .reportCount(item.reportCount())
                .status(item.status().getDescription())
                .navigationType(item.navigationType().name())
                .build();
    }
}
