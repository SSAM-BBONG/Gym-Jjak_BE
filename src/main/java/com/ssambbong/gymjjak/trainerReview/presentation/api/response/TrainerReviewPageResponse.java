package com.ssambbong.gymjjak.trainerReview.presentation.api.response;

import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewListResult;
import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewSummary;

import java.util.List;
import java.util.Map;

public record TrainerReviewPageResponse(
        double averageRating,
        long reviewCount,
        Map<Integer, Long> ratingDistribution,
        List<TrainerReviewItemResponse> reviews,
        Long nextCursor,
        Integer nextCursorRating,
        boolean hasNext
) {
    public static TrainerReviewPageResponse of(TrainerReviewSummary summary, TrainerReviewListResult result) {
        return new TrainerReviewPageResponse(
                summary.averageRating(),
                summary.reviewCount(),
                summary.ratingDistribution(),
                result.reviews().stream().map(TrainerReviewItemResponse::from).toList(),
                result.nextCursor(),
                result.nextCursorRating(),
                result.hasNext()
        );
    }
}
