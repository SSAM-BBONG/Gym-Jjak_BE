package com.ssambbong.gymjjak.pt.ptRecommendation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptRecommendation.application.result.PtRecommendationResult;

import java.util.List;

// 클라이언트 응답 DTO. recommendations 순서 = AI가 매긴 추천 순위(1순위부터).
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
