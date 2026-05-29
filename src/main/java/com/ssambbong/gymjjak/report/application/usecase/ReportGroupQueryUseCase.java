package com.ssambbong.gymjjak.report.application.usecase;

import com.ssambbong.gymjjak.report.application.query.AdminReportDetailResult;
import com.ssambbong.gymjjak.report.application.query.AdminReportListQuery;
import com.ssambbong.gymjjak.report.application.query.AdminReportListResult;

public interface ReportGroupQueryUseCase {

    // 신고 목록 조회
    AdminReportListResult findReportGroups(AdminReportListQuery query);

    // 신고 상세 사유 조회
    AdminReportDetailResult findReportDetail(Long reportGroupId);
}
