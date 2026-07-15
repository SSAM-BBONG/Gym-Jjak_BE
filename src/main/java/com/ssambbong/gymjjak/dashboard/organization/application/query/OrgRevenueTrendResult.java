package com.ssambbong.gymjjak.dashboard.organization.application.query;

import java.util.List;

public record OrgRevenueTrendResult(
        List<TrendPoint> weekly,
        List<TrendPoint> monthly,
        List<TrendPoint> threeMonthly,
        List<TrendPoint> sixMonthly
) {}
