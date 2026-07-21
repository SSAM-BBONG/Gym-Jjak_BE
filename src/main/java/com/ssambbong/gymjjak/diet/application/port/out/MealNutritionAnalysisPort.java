package com.ssambbong.gymjjak.diet.application.port.out;

import com.ssambbong.gymjjak.diet.application.result.MealNutritionSummary;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MealNutritionAnalysisPort {
    // AI 서버의 분석 응답이 도착할 때까지 현재 MVC 요청 스레드에서 동기 대기한다.
    AnalysisResult analyze(AnalysisRequest request);

    record AnalysisRequest(
            String imageUrl,
            String mealType,
            LocalDateTime mealTime,
            NutritionGoalSnapshot nutritionGoal,
            MealNutritionSummary todayIntake
    ) {
    }

    record NutritionGoalSnapshot(Long protein, Long carbohydrate, Long fat, Long kcal) {
    }

    record AnalysisResult(
            String menu,
            Long kcal,
            BigDecimal carbohydrate,
            BigDecimal protein,
            BigDecimal fat,
            String evaluation,
            BigDecimal confidence,
            List<String> warnings
    ) {
        public AnalysisResult {
            warnings = warnings == null ? List.of() : List.copyOf(warnings);
        }
    }
}
