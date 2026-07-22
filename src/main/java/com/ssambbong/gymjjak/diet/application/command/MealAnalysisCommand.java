package com.ssambbong.gymjjak.diet.application.command;

import com.ssambbong.gymjjak.diet.domain.model.MealType;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record MealAnalysisCommand(
        Long userId,
        MealType mealType,
        LocalDateTime mealTime,
        String menu,
        Long kcal,
        BigDecimal carbohydrate,
        BigDecimal protein,
        BigDecimal fat,
        MealImageMetadataCommand file
) {
    // 등록 요청에 영양성분이 하나라도 들어오면 AI 구독 권한 검증 대상이다.
    public boolean hasMacronutrients() {
        return carbohydrate != null || protein != null || fat != null;
    }
}
