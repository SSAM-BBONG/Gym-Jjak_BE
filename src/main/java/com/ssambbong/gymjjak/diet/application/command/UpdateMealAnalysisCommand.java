package com.ssambbong.gymjjak.diet.application.command;

import com.ssambbong.gymjjak.diet.domain.model.MealType;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record UpdateMealAnalysisCommand(
        Long userId,
        MealType mealType,
        boolean mealTypePresent,
        LocalDateTime mealTime,
        boolean mealTimePresent,
        String menu,
        boolean menuPresent,
        Long kcal,
        boolean kcalPresent,
        BigDecimal carbohydrate,
        boolean carbohydratePresent,
        BigDecimal protein,
        boolean proteinPresent,
        BigDecimal fat,
        boolean fatPresent,
        Long fileId,
        boolean fileIdPresent
) {
    // 값이 null이어도 필드가 전달됐다면 영양성분 제거 요청이므로 권한 검증 대상이다.
    public boolean updatesMacronutrients() {
        return carbohydratePresent || proteinPresent || fatPresent;
    }
}
