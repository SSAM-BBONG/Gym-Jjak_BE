package com.ssambbong.gymjjak.dashboard.organization.application.query;

public record OrgPtCourseResult(
        Long ptCourseId,
        String title,
        int price,
        int totalSessionCount,
        String status,
        String trainerName,
        long currentStudentCount
) {
}
