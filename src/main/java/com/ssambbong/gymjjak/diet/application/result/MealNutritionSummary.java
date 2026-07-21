package com.ssambbong.gymjjak.diet.application.result;

import java.math.BigDecimal;

public record MealNutritionSummary(
        Long kcal,
        BigDecimal carbohydrate,
        BigDecimal protein,
        BigDecimal fat
) {
    public static MealNutritionSummary empty() {
        return new MealNutritionSummary(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
