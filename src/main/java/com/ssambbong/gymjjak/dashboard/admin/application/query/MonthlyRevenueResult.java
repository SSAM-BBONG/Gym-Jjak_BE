package com.ssambbong.gymjjak.dashboard.admin.application.query;

import lombok.Builder;

@Builder
public record MonthlyRevenueResult(
        String month,
        long ptCommissionRevenue,
        long subscriptionRevenue,
        long totalRevenue
) {
}
