package com.ssambbong.gymjjak.category.application.usecase;

import java.time.LocalDateTime;
import java.util.List;

public interface CategoryQueryUseCase {

    List<CategoryView> handle();

    record CategoryView(
            Long categoryId,
            String name,
            LocalDateTime createdAt,
            long usageCount
    ) {}
}
