package com.ssambbong.gymjjak.pt.feedback.application.usecase;

import com.ssambbong.gymjjak.pt.feedback.application.command.CreateFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.command.UpdateFeedbackCommand;

public interface FeedbackCommandUseCase {

    // 피드백 등록
    Long createFeedback(CreateFeedbackCommand command);

    // 피드백 수정
    Long updateFeedback(UpdateFeedbackCommand command);
}
