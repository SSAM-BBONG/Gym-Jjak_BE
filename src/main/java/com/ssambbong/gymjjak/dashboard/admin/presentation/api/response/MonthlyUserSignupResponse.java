package com.ssambbong.gymjjak.dashboard.admin.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.admin.application.query.MonthlyUserSignupResult;
import lombok.Builder;

@Builder
public record MonthlyUserSignupResponse(
        String month,
        long count
) {

    public static MonthlyUserSignupResponse from(
            MonthlyUserSignupResult result
    ) {
        return MonthlyUserSignupResponse.builder()
                .month(result.month())
                .count(result.count())
                .build();
    }
}
