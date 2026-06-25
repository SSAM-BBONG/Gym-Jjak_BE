package com.ssambbong.gymjjak.pt.feedback.presentation.api.request;

import com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest;
import com.ssambbong.gymjjak.pt.feedback.application.command.CreateFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateFeedbackRequest(
        @NotNull Long ptCurriculumId,
        @NotBlank String content,
        @NotEmpty @Size(max = 2) @Valid List<@NotNull MediaRequest> media
) {
    public CreateFeedbackCommand toCommand(Long userId, Long ptReservationId) {
        return new CreateFeedbackCommand(
                userId,
                ptReservationId,
                ptCurriculumId,
                content,
                media.stream()
                        .map(m -> new CreateFeedbackCommand.MediaCommand(
                                new UploadedFileMetadataCommand(
                                        m.file().fileKey(),
                                        m.file().originalName(),
                                        m.file().contentType(),
                                        m.file().fileSize()
                                ),
                                m.mediaType()
                        ))
                        .toList()
        );
    }

    public record MediaRequest(
            @NotNull @Valid UploadedFileMetadataRequest file,   // Long fileId → UploadedFileMetadataRequest file
            @NotNull FeedbackMediaType mediaType
    ) {}
}
