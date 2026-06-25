package com.ssambbong.gymjjak.trainerReview.presentation.api.response;

import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewListResult;

import java.util.List;

public record TrainerReviewListResponse(
        List<TrainerReviewItemResponse> reviews,
        Long nextCursor,
        Integer nextCursorRating,
        boolean hasNext
) {
    public static TrainerReviewListResponse from(TrainerReviewListResult result) {
        return new TrainerReviewListResponse(
                result.reviews().stream().map(TrainerReviewItemResponse::from).toList(),
                result.nextCursor(),
                result.nextCursorRating(),
                result.hasNext()
        );
    }
}
