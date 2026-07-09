package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgTrendResult;

import java.util.List;

public record OrgTrendResponse(
        List<TrendPointResponse> weekly,
        List<TrendPointResponse> monthly,
        List<TrendPointResponse> threeMonthly,
        List<TrendPointResponse> sixMonthly
) {
    public static OrgTrendResponse from(OrgTrendResult result) {
        return new OrgTrendResponse(
                result.weekly().stream().map(TrendPointResponse::from).toList(),
                result.monthly().stream().map(TrendPointResponse::from).toList(),
                result.threeMonthly().stream().map(TrendPointResponse::from).toList(),
                result.sixMonthly().stream().map(TrendPointResponse::from).toList()
        );
    }
}
