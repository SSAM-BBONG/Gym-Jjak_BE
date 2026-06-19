package com.ssambbong.gymjjak.pt.feedback.application.usecase;

import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;

import java.util.List;

public interface FeedbackCommandUseCase {

    // 피드백 등록
    Long createFeedback(Long userId, Long ptReservationId, CreateFeedbackCommand command);

    record CreateFeedbackCommand (
            Long ptCurriculumId,
            String content,
            List<MediaCommand> media
    ) {}

    record MediaCommand(
            Long fileId,
            FeedbackMediaType mediaType
    ) {}
}
