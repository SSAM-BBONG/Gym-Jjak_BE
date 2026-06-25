package com.ssambbong.gymjjak.trainerReview.presentation.api.response;

public record UpdateTrainerReviewResponse(Long trainerReviewId) {

    public static UpdateTrainerReviewResponse from(Long reviewId) {
        return new UpdateTrainerReviewResponse(reviewId);
    }
}
