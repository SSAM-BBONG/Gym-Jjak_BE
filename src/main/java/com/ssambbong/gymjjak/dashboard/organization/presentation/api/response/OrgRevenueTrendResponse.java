package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgRevenueTrendResult;

import java.util.List;

public record OrgRevenueTrendResponse(
        List<RevenuePointResponse> weekly,
        List<RevenuePointResponse> monthly,
        List<RevenuePointResponse> threeMonthly,
        List<RevenuePointResponse> sixMonthly
) {
    public static OrgRevenueTrendResponse from(OrgRevenueTrendResult result) {
        return new OrgRevenueTrendResponse(
                result.weekly().stream().map(RevenuePointResponse::from).toList(),
                result.monthly().stream().map(RevenuePointResponse::from).toList(),
                result.threeMonthly().stream().map(RevenuePointResponse::from).toList(),
                result.sixMonthly().stream().map(RevenuePointResponse::from).toList()
        );
    }
}
