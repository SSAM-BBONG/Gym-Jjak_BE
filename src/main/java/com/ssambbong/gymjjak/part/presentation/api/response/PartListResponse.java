package com.ssambbong.gymjjak.part.presentation.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssambbong.gymjjak.part.application.usecase.PartQueryUseCase;

import java.time.LocalDateTime;

public record PartListResponse(
        Long partId,
        String name,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        long usageCount
) {
    public static PartListResponse from(PartQueryUseCase.PartView view) {
        return new PartListResponse(view.partId(), view.name(), view.createdAt(), view.usageCount());
    }
}
