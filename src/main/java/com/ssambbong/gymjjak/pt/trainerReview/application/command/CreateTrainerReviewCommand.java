package com.ssambbong.gymjjak.pt.trainerReview.application.command;

public record CreateTrainerReviewCommand(
        Long userId,
        Long ptCourseId,
        Long ptReservationId,
        int rating,
        String content
) {}
