package com.ssambbong.gymjjak.dashboard.organization.application.usecase;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgPtCourseResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgStatsResult;
import com.ssambbong.gymjjak.dashboard.organization.application.query.TrainerClientResult;

import java.util.List;

public interface OrganizationDashboardUseCase {

    OrgStatsResult getStats(Long userId);

    List<TrainerClientResult> getTrainerClients(Long userId);

    List<OrgPtCourseResult> getPtCourses(Long userId);
}
