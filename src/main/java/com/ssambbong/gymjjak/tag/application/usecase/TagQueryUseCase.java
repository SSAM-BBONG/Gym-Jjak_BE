package com.ssambbong.gymjjak.tag.application.usecase;

import java.time.LocalDateTime;
import java.util.List;

public interface TagQueryUseCase {

    List<TagView> handle();

    record TagView(
            Long tagId,
            String name,
            LocalDateTime createdAt,
            long usageCount
    ) {}
}
