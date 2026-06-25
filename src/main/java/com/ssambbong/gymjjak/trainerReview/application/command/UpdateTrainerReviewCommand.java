package com.ssambbong.gymjjak.trainerReview.application.command;

public record UpdateTrainerReviewCommand(
        Long userId,
        Long reviewId,
        int rating,
        String content
) {}
