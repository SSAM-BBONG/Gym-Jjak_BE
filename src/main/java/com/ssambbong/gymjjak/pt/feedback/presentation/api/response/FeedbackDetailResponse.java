package com.ssambbong.gymjjak.pt.feedback.presentation.api.response;

import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackQueryUseCase;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;

import java.time.LocalDate;
import java.util.List;

public record FeedbackDetailResponse(
        int sessionNo,
        String curriculumTitle,
        String content,
        List<MediaSummary> mediaList,
        LocalDate createdAt
) {
    public static FeedbackDetailResponse from(FeedbackQueryUseCase.FeedbackDetailView view) {
        List<MediaSummary> mediaList = view.mediaList().stream()
                .map(m -> new MediaSummary(m.feedbackMediaId(), m.mediaType(), m.fileUrl()))
                .toList();
        return new FeedbackDetailResponse(
                view.sessionNo(),
                view.curriculumTitle(),
                view.content(),
                mediaList,
                view.createdAt()
        );
    }

    public record MediaSummary(Long feedbackMediaId, FeedbackMediaType mediaType, String fileUrl) {}
}
