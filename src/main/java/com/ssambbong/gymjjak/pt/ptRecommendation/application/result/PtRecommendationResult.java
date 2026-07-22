package com.ssambbong.gymjjak.pt.ptRecommendation.application.result;

import java.util.List;

// PtRecommendationUseCase의 응답. recommendations는 AI가 매긴 순위 순서 그대로다(최대 3개).
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
