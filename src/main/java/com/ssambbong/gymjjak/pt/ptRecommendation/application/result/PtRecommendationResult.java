package com.ssambbong.gymjjak.pt.ptRecommendation.application.result;

import java.util.List;

public record PtRecommendationResult(
        List<RecommendedCourseResult> recommendations
) {
    public record RecommendedCourseResult(
            Long courseId,
            String courseName,
            Long trainerId,
            String trainerName,
            String reason
    ) {}
}
