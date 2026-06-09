package com.ssambbong.gymjjak.report.application.query;

import java.util.List;

// 목록 조회 전체 결과 감싸는 객체
public record AdminReportListResult(
        // 신고 그룹 목록
        List<AdminReportListItem> items,
        int page,
        // 한 페이지 보여줄 수
        int size,
        // 대이터 수
        long totalElements,
        int totalPages
) {
}
