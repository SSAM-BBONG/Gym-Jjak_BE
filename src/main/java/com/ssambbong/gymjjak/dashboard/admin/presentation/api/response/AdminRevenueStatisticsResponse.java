package com.ssambbong.gymjjak.dashboard.admin.presentation.api.response;


import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminRevenueStatisticsResult;
import lombok.Builder;

import java.util.List;

@Builder
public record AdminRevenueStatisticsResponse(
        // 6개월 매출 통계
        List<MonthlyRevenueResponse> monthlyRevenues
) {

    public static AdminRevenueStatisticsResponse from(AdminRevenueStatisticsResult result) {
        return AdminRevenueStatisticsResponse.builder()
                .monthlyRevenues(
                        result.monthlyRevenues().stream()
                                .map(MonthlyRevenueResponse::from)
                                .toList()
                )
                .build();
    }
}
