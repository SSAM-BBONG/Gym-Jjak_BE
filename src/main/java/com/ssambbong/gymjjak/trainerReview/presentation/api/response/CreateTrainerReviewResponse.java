package com.ssambbong.gymjjak.trainerReview.presentation.api.response;

public record CreateTrainerReviewResponse(Long trainerReviewId) {

    public static CreateTrainerReviewResponse from(Long trainerReviewId) {
        return new CreateTrainerReviewResponse(trainerReviewId);
    }
}
