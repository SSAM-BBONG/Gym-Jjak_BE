package com.ssambbong.gymjjak.pt.feedback.application.command;

public record DeleteFeedbackCommand(
        Long userId,
        Long ptReservationId,
        Long feedbackId
) {}
