package com.ssambbong.gymjjak.diet.application.query;

public record MealPageQuery(
        Long userId,
        int page,
        int size
) {
}
