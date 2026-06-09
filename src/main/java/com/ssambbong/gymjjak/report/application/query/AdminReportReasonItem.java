package com.ssambbong.gymjjak.report.application.query;

import com.ssambbong.gymjjak.report.domain.model.ReportReasonType;
import com.ssambbong.gymjjak.report.domain.model.ReportStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public record AdminReportReasonItem(
        Long reportId,
        String reporterUsername, // 신고자 아이디
        ReportReasonType reason, // 선택 신고 사유
        String detail, // 상세 사유
        LocalDateTime reportedAt,
        ReportStatus status // 단건 신고 처리 상태
) {

}
