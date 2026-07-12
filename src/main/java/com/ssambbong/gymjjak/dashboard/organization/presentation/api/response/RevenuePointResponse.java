package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.TrendPoint;

import java.time.LocalDate;

public record RevenuePointResponse(LocalDate date, long amount) {
    public static RevenuePointResponse from(TrendPoint point) {
        return new RevenuePointResponse(point.date(), point.count());
    }
}
