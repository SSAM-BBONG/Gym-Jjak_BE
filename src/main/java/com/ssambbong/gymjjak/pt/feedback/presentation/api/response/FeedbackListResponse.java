package com.ssambbong.gymjjak.pt.feedback.presentation.api.response;

import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackQueryUseCase;

import java.time.LocalDate;

public record FeedbackListResponse(
        Long ptCurriculumId,
        int sessionNo,
        String title,
        FeedbackSummary feedbacks // null 가능
) {

    public static FeedbackListResponse from(FeedbackQueryUseCase.FeedbackListView view) {
        FeedbackSummary summary = (view.feedback() == null) ? null
                : new FeedbackSummary(
                        view.feedback().feedbackId(),
                        view.feedback().content(),
                        view.feedback().createdAt()
        );
        return new FeedbackListResponse(
                view.ptCurriculumId(),
                view.sessionNo(),
                view.title(),
                summary
        );
    }

    public record FeedbackSummary(
            Long feedbackId,
            String content,
            LocalDate createdAt
    ) {}
}
