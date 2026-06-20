package com.ssambbong.gymjjak.pt.feedback.presentation.api.response;

public record CreateFeedbackResponse(Long feedbackId) {
    public static CreateFeedbackResponse from(Long feedbackId) {
        return new CreateFeedbackResponse(feedbackId);
    }
}
