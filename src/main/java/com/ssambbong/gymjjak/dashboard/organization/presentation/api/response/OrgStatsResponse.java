package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgStatsResult;

public record OrgStatsResponse(
        long trainerCount,
        long totalUserCount,
        long currentUserCount
) {
    public static OrgStatsResponse from(OrgStatsResult result) {
        return new OrgStatsResponse(
                result.trainerCount(),
                result.totalUserCount(),
                result.currentUserCount()
        );
    }
}
