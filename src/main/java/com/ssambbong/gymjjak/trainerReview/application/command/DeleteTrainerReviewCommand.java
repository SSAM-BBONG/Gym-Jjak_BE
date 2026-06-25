package com.ssambbong.gymjjak.trainerReview.application.command;

public record DeleteTrainerReviewCommand(
        Long userId,
        Long reviewId
) {}
