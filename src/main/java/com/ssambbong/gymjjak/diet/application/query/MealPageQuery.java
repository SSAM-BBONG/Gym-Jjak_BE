package com.ssambbong.gymjjak.diet.application.query;

import java.time.LocalDate;

public record MealPageQuery(
        Long requesterUserId,
        Long targetUserId,
        int page,
        int size,
        LocalDate date
) {
    public MealPageQuery(Long userId, int page, int size) {
        this(userId, userId, page, size, null);
    }
}
