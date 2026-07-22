package com.ssambbong.gymjjak.pt.ptRecommendation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptRecommendation.application.result.PtRecommendationResult;

import java.util.List;

public record PtRecommendationResponse(
        List<RecommendedPtCourseResponse> recommendations
) {
    public static PtRecommendationResponse from(PtRecommendationResult result) {
        return new PtRecommendationResponse(
                result.recommendations().stream()
                        .map(RecommendedPtCourseResponse::from)
                        .toList());
    }

    public record RecommendedPtCourseResponse(
            Long courseId,
            String courseName,
            Long trainerId,
            String trainerName,
            String reason
    ) {
        static RecommendedPtCourseResponse from(PtRecommendationResult.RecommendedCourseResult result) {
            return new RecommendedPtCourseResponse(
                    result.courseId(), result.courseName(),
                    result.trainerId(), result.trainerName(), result.reason());
        }
    }
}
