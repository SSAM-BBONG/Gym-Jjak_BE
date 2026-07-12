package com.ssambbong.gymjjak.dashboard.organization.application.query;

import java.util.List;

public record OrgSalesResult(
        long totalRevenue,
        long thisMonthRevenue,
        double monthOverMonthRate,
        List<TrendPoint> monthlyRevenue,
        List<TrainerSalesResult> trainerSales
) {}
