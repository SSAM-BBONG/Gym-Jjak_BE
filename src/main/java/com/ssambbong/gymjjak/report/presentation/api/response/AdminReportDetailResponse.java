package com.ssambbong.gymjjak.report.presentation.api.response;

import com.ssambbong.gymjjak.report.application.query.AdminReportDetailResult;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 신고 상세 모달 페이지 전체 response 객체
 * @param reportGroupId : 해당 신고그룹 pk
 * @param status : 해당 신고그룹의 상태
 * @param reports : 개별 신고들을 묶은 List
 */
@Builder
public record AdminReportDetailResponse(
        Long reportGroupId,
        String status,
        List<AdminReportReasonItemResponse> reports
) {

    public static AdminReportDetailResponse from(AdminReportDetailResult result) {
        return AdminReportDetailResponse.builder()
                .reportGroupId(result.reportGroupId())
                .status(result.status().getDescription())
                .reports(
                        result.reports().stream()
                                .map(AdminReportReasonItemResponse::from)
                                .toList()
                )
                .build();
    }
}
