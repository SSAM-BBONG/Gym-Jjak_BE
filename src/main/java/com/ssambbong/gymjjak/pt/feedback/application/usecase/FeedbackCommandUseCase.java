package com.ssambbong.gymjjak.pt.feedback.application.usecase;

import com.ssambbong.gymjjak.pt.feedback.application.command.CreateFeedbackCommand;

public interface FeedbackCommandUseCase {

    // 피드백 등록
    Long createFeedback(CreateFeedbackCommand command);
}
