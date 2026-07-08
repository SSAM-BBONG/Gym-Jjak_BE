package com.ssambbong.gymjjak.dashboard.admin.application.query;

import lombok.Builder;

@Builder
public record MonthlyUserSignupResult(
        String month,
        long count
) {
}
