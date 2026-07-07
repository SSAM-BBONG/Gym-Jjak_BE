package com.ssambbong.gymjjak.dashboard.admin.application.query;

import lombok.Builder;

import java.util.List;

@Builder
public record AdminMemberStatisticsResult(
        long totalUserCount,
        long totalTrainerCount,
        long totalOrganizationCount,
        List<MonthlyUserSignupResult> monthlyUserSignups
) {
}
