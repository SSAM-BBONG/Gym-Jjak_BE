package com.ssambbong.gymjjak.dashboard.admin.application.query;

import lombok.Builder;

@Builder
public record AdminPendingStatisticsResult(
        long pendingTrainerApplicationCount,
        long pendingOrganizationApplicationCount
) {
}
