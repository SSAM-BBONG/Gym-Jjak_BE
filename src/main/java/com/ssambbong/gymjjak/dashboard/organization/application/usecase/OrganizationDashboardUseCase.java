package com.ssambbong.gymjjak.dashboard.organization.application.usecase;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgStatsResult;

public interface OrganizationDashboardUseCase {

    OrgStatsResult getStats(Long userId);
}
