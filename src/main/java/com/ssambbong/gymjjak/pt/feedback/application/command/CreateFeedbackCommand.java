package com.ssambbong.gymjjak.pt.feedback.application.command;

import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;

import java.util.List;

public record CreateFeedbackCommand(
        Long userId,
        Long ptReservationId,
        Long ptCurriculumId,
        String content,
        List<MediaCommand> media
) {
    public record MediaCommand(
            Long fileId,
            FeedbackMediaType mediaType
    ) {}
}
