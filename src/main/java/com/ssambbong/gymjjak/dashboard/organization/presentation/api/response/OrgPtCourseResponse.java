package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgPtCourseResult;

public record OrgPtCourseResponse(
        Long ptCourseId,
        String title,
        String status,
        String trainerName,
        long currentStudentCount
) {
    public static OrgPtCourseResponse from(OrgPtCourseResult result) {
        return new OrgPtCourseResponse(
                result.ptCourseId(),
                result.title(),
                result.status(),
                result.trainerName(),
                result.currentStudentCount()
        );
    }
}
