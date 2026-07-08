package com.ssambbong.gymjjak.dashboard.admin.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.admin.application.query.AdminPendingStatisticsResult;
import lombok.Builder;

@Builder
public record AdminPendingStatisticsResponse(
        long pendingTrainerApplicationCount,
        long pendingOrganizationApplicationCount
) {

    public static AdminPendingStatisticsResponse from(
            AdminPendingStatisticsResult result
    ) {
        return AdminPendingStatisticsResponse.builder()
                .pendingTrainerApplicationCount(
                        result.pendingTrainerApplicationCount()
                )
                .pendingOrganizationApplicationCount(
                        result.pendingOrganizationApplicationCount()
                )
                .build();
    }
}
