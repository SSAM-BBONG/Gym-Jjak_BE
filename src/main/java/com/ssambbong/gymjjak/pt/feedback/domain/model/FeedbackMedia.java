package com.ssambbong.gymjjak.pt.feedback.domain.model;

public class FeedbackMedia {

    private final Long id;
    private final Long feedbackId;
    private final FeedbackMediaType mediaType;
    private final Long fileId;

    public FeedbackMedia(Long id, Long feedbackId, FeedbackMediaType mediaType, Long fileId) {
        this.id = id;
        this.feedbackId = feedbackId;
        this.mediaType = mediaType;
        this.fileId = fileId;
    }

    // DB 복원 시
    public static FeedbackMedia restore(Long id, Long feedbackId, FeedbackMedia mediaType, Long fileId) {
        return new FeedbackMedia(id, feedbackId, mediaType, fileId);
    }

    public Long getId() {
        return id;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public FeedbackMediaType getMediaType() {
        return mediaType;
    }

    public Long getFileId() {
        return fileId;
    }
}
