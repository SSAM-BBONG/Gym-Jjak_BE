package com.ssambbong.gymjjak.report.presentation.api.response;

import com.ssambbong.gymjjak.report.application.query.AdminReportListResult;
import lombok.Builder;

import java.util.List;

@Builder
public record AdminReportListResponse(
        List<AdminReportListItemResponse> reports,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static AdminReportListResponse from(AdminReportListResult result) {
        return AdminReportListResponse.builder()
                .reports(
                        result.items().stream()
                                .map(AdminReportListItemResponse::from)
                                .toList()
                )
                .page(result.page())
                .size(result.size())
                .totalElements(result.totalElements())
                .totalPages(result.totalPages())
                .build();
    }
}
