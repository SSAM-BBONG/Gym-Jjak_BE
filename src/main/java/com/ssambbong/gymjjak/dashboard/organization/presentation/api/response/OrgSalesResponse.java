package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgSalesResult;

import java.util.List;

public record OrgSalesResponse(
        long totalRevenue,
        long thisMonthRevenue,
        double monthOverMonthRate,
        List<RevenuePointResponse> monthlyRevenue,
        List<TrainerSalesResponse> trainerSales
) {
    public static OrgSalesResponse from(OrgSalesResult result) {
        return new OrgSalesResponse(
                result.totalRevenue(),
                result.thisMonthRevenue(),
                result.monthOverMonthRate(),
                result.monthlyRevenue().stream().map(RevenuePointResponse::from).toList(),
                result.trainerSales().stream().map(TrainerSalesResponse::from).toList()
        );
    }
}
