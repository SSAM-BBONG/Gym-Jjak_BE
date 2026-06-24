package com.ssambbong.gymjjak.pt.feedback.application.command;

import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;

import java.util.List;

public record UpdateFeedbackCommand(
        Long userId,
        Long ptReservationId,
        Long feedbackId,
        String content,
        List<MediaCommand> media
) {
    // create와 동일하게 파일 메타데이터를 직접 받아 파일 등록 후 저장
    public record MediaCommand(
            UploadedFileMetadataCommand file,
            FeedbackMediaType mediaType
    ) {}
}
