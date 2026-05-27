package com.ssambbong.gymjjak.report.application.query;

import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;

// 조회 조건을 담는 객체
public record AdminReportListQuery(
        ReportTargetType  targetType,
        int page,
        int size
) {
}
