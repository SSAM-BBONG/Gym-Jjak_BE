package com.ssambbong.gymjjak.report.application.query;

import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;

import java.util.List;

public record AdminReportDetailResult(
        // TODO : 혹시 신고 그룹에 대한 추가 정보가 있으면 더 좋을까?
        //  근데 이미 목록조회에서 뿌려주고 있긴 한데
        Long reportGroupId,
        ReportGroupReviewStatus status,
        List<AdminReportReasonItem> reports
) {
}
