package com.ssambbong.gymjjak.pt.trainerReview.presentation.api.response;

import com.ssambbong.gymjjak.pt.trainerReview.application.query.TrainerReviewSummary;

import java.util.Map;

public record TrainerReviewSummaryResponse(
        String trainerName,
        String introduction,
        double averageRating,
        long reviewCount,
        Map<Integer, Long> ratingDistribution
) {
    public static TrainerReviewSummaryResponse from(TrainerReviewSummary summary) {
        return new TrainerReviewSummaryResponse(
                summary.trainerName(),
                summary.introduction(),
                summary.averageRating(),
                summary.reviewCount(),
                summary.ratingDistribution()
        );
    }
}
