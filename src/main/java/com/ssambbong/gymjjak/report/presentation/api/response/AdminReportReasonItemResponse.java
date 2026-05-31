package com.ssambbong.gymjjak.report.presentation.api.response;

import com.ssambbong.gymjjak.report.application.query.AdminReportReasonItem;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 신고 상세 보기 - 개별 신고 블록
 * @param reportId : 신고자 pk값
 * @param reporterUsername : 신고자의 id
 * @param reason : 신고 사유 enum 값, ex) 욕설, 광고, 음란물
 * @param detail : 신고 상세 사유
 * @param reportedAt : 신고일시
 * @param status : 개별 신고 처리 상태
 */
@Builder
public record AdminReportReasonItemResponse(
        Long reportId,
        String reporterUsername,
        String reason,
        String detail,
        LocalDateTime reportedAt,
        String status
) {
    // 신고 상세 조회 반환용 Builder
    public static  AdminReportReasonItemResponse from(AdminReportReasonItem item){
        return AdminReportReasonItemResponse.builder()
                .reportId(item.reportId())
                .reporterUsername(item.reporterUsername())
                .reason(item.reason().getDescription())
                .detail(item.detail())
                .reportedAt(item.reportedAt())
                .status(item.status().getDescription())
                .build();
    }
}
