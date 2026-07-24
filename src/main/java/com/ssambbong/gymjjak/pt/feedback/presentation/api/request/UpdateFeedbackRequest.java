package com.ssambbong.gymjjak.pt.feedback.presentation.api.request;

import com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest;
import com.ssambbong.gymjjak.pt.feedback.application.command.UpdateFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateFeedbackRequest(
        @NotBlank String content,
        @Size(min = 1, max = 2) @Valid List<@NotNull MediaRequest> media
) {
    public UpdateFeedbackCommand toCommand(Long userId, Long ptReservationId, Long feedbackId) {
        List<UpdateFeedbackCommand.MediaCommand> mediaCommands = (media == null) ? null :
                media.stream()
                        .map(m -> new UpdateFeedbackCommand.MediaCommand(
                                new UploadedFileMetadataCommand(
                                        m.file().fileKey(),
                                        m.file().originalName(),
                                        m.file().contentType(),
                                        m.file().fileSize()
                                ),
                                m.mediaType()
                        ))
                        .toList();
        return new UpdateFeedbackCommand(userId, ptReservationId, feedbackId, content, mediaCommands);
    }

    // create와 동일하게 파일 메타데이터 전체를 받음 — fileId 방식 사용 안 함
    public record MediaRequest(
            @NotNull @Valid UploadedFileMetadataRequest file,
            @NotNull FeedbackMediaType mediaType
    ) {}
}
