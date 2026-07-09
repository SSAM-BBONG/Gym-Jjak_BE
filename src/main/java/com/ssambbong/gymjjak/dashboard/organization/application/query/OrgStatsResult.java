package com.ssambbong.gymjjak.dashboard.organization.application.query;

public record OrgStatsResult(
        long trainerCount,
        long totalUserCount,
        long currentUserCount,
        OrgTrendResult trend
) {
}
