package com.ssambbong.gymjjak.report.presentation.api.response;

import com.ssambbong.gymjjak.report.application.query.AdminReportListResult;
import lombok.Builder;

import java.util.List;

/**
 * 관리자 신고 그룹 목록 조회 전체 response 객체
 * @param reports : 신고 그룹 페이지에 보여줄 1행의 신고 response List
 * @param page : page 기능, 현재 페이지
 * @param size : 한 페이지에 담길 데이터 개수
 * @param totalElements : 총 데이터 개수
 * @param totalPages : 총 page 수
 */
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
