package com.ssambbong.gymjjak.pt.feedback.presentation.api.response;

public record UpdateFeedbackResponse(Long feedbackId) {

    public static UpdateFeedbackResponse from(Long feedbackId) {
        return new UpdateFeedbackResponse(feedbackId);
    }
}
