package com.ssambbong.gymjjak.part.application.usecase;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public interface PartQueryUseCase {

    List<PartView> handle();

    record PartView(
            Long partId,
            String name,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime createdAt,
            long usageCount
    ) {}
}
