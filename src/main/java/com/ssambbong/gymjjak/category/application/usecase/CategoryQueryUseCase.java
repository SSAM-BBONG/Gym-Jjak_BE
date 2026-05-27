package com.ssambbong.gymjjak.category.application.usecase;

import java.util.List;

public interface CategoryQueryUseCase {

    List<CategoryView> handle();

    record CategoryView(
            Long categoryId,
            String name
    ) {

    }
}
