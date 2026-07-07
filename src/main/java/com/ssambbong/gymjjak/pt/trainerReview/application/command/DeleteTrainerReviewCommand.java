package com.ssambbong.gymjjak.pt.trainerReview.application.command;

public record DeleteTrainerReviewCommand(
        Long userId,
        Long reviewId
) {}
