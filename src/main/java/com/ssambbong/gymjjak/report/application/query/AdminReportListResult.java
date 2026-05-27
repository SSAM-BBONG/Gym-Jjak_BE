package com.ssambbong.gymjjak.report.application.query;

import java.util.List;

// 목록 조회 전체 결과 감싸는 객체
public record AdminReportListResult(
        List<AdminReportListItem> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
