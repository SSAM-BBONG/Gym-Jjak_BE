package com.ssambbong.gymjjak.part.application.usecase;

import java.time.LocalDateTime;
import java.util.List;

public interface PartQueryUseCase {

    List<PartView> handle();

    record PartView(
            Long partId,
            String name,
            LocalDateTime createdAt,
            long usageCount
    ) {}
}
