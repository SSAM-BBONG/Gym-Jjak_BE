package com.ssambbong.gymjjak.dashboard.admin.application.query;


import java.util.List;

public record AdminRevenueStatisticsResult(
        // 최근 6개월 월별 매출 통계
        List<MonthlyRevenueResult> monthlyRevenues
) {
}
