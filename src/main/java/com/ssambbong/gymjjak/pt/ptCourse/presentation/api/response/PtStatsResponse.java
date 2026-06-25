package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;

public record PtStatsResponse(
        long organizationCount,
        long activeTrainerCount,
        long inProgressPtCount,
        Double averageSatisfaction
) {
    public static PtStatsResponse from(PtCourseQueryUseCase.PtStatsView view) {
        Double avg = view.averageSatisfaction();
        Double rounded = avg != null ? Math.round(avg * 10.0) / 10.0 : null;
        return new PtStatsResponse(
                view.organizationCount(),
                view.activeTrainerCount(),
                view.inProgressPtCount(),
                rounded
        );
    }
}
