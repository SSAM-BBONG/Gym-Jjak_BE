package com.ssambbong.gymjjak.pt.feedback.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedbackMedia {

    private final Long id;
    private final Long feedbackId;
    private final FeedbackMediaType mediaType;
    private final Long fileId;

    public static FeedbackMedia create(Long feedbackId, FeedbackMediaType mediaType, Long fileId) {
        return FeedbackMedia.builder()
                .feedbackId(feedbackId)
                .mediaType(mediaType)
                .fileId(fileId)
                .build();
    }

    public static FeedbackMedia restore(Long id, Long feedbackId, FeedbackMediaType mediaType, Long fileId) {
        return FeedbackMedia.builder()
                .id(id)
                .feedbackId(feedbackId)
                .mediaType(mediaType)
                .fileId(fileId)
                .build();
    }
}
