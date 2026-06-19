package com.ssambbong.gymjjak.pt.feedback.presentation.api.request;

import com.ssambbong.gymjjak.pt.feedback.application.command.CreateFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

public record CreateFeedbackRequest(
        @NotNull Long ptCurriculumId,
        @NotBlank String content,
        @NotEmpty @Valid List<@NotNull MediaRequest> media
) {
    public CreateFeedbackCommand toCommand(Long userId, Long ptReservationId) {
        return new CreateFeedbackCommand(
                userId,
                ptReservationId,
                ptCurriculumId,
                content,
                media.stream()
                        .map(m -> new CreateFeedbackCommand.MediaCommand(m.fileId(), m.mediaType()))
                        .toList()
        );
    }

    public record MediaRequest(
            @NotNull Long fileId,
            @NotNull FeedbackMediaType mediaType
    ) {}
}
