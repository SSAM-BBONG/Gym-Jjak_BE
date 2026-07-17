package com.ssambbong.gymjjak.dashboard.admin.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.admin.application.query.MonthlyRevenueResult;
import lombok.Builder;

@Builder
public record MonthlyRevenueResponse(
        String month,
        long ptCommissionRevenue,
        long subscriptionRevenue,
        long totalRevenue
) {

    public static MonthlyRevenueResponse from(MonthlyRevenueResult result) {
        return MonthlyRevenueResponse.builder()
                .month(result.month())
                .ptCommissionRevenue(result.ptCommissionRevenue())
                .subscriptionRevenue(result.subscriptionRevenue())
                .totalRevenue(result.totalRevenue())
                .build();
    }
}
