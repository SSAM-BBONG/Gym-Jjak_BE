package com.ssambbong.gymjjak.pt.feedback.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FeedbackMedia {

    private final Long id;
    private final Long feedbackId;
    private final FeedbackMediaType mediaType;
    private final Long fileId;

    @Builder(access = AccessLevel.PUBLIC)
    private FeedbackMedia(Long id, Long feedbackId, FeedbackMediaType mediaType, Long fileId) {
        this.id = id;
        this.feedbackId = feedbackId;
        this.mediaType = mediaType;
        this.fileId = fileId;
    }

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
