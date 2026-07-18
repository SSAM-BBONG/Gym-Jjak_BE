package com.ssambbong.gymjjak.diet.application.result;

import java.util.List;
import java.util.function.Function;

public record MealPageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
    public <R> MealPageResult<R> map(Function<T, R> mapper) {
        return new MealPageResult<>(
                content.stream().map(mapper).toList(),
                page,
                size,
                totalElements,
                totalPages,
                hasNext
        );
    }
}
