package com.ssambbong.gymjjak.diet.application.port.out;

import com.ssambbong.gymjjak.diet.application.result.MealNutritionSummary;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import reactor.core.publisher.Mono;

public interface MealNutritionAnalysisPort {
    // AI 서버 호출은 네트워크 대기 동안 요청 스레드를 점유하지 않도록 비동기 결과를 반환한다.
    Mono<AnalysisResult> analyze(AnalysisRequest request);

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
