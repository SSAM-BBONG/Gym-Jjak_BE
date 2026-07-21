package com.ssambbong.gymjjak.diet.application.query;

import java.time.LocalDate;

public record MealPageQuery(
        Long userId,
        int page,
        int size,
        LocalDate date
) {
    public MealPageQuery(Long userId, int page, int size) {
        this(userId, page, size, null);
    }
}
