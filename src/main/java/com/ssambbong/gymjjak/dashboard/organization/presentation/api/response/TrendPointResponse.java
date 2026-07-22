package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.TrendPoint;

import java.time.LocalDate;

public record TrendPointResponse(LocalDate date, long count) {
    public static TrendPointResponse from(TrendPoint point) {
        return new TrendPointResponse(point.date(), point.value());
    }
}
