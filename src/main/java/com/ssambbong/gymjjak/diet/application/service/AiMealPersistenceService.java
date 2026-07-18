package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.AiMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.out.MealAnalysisPort;
import com.ssambbong.gymjjak.diet.application.port.out.MealNutritionAnalysisPort;
import com.ssambbong.gymjjak.diet.application.result.AiMealAnalysisResult;
import com.ssambbong.gymjjak.diet.domain.exception.AiMealAnalysisException;
import com.ssambbong.gymjjak.diet.domain.exception.MealAnalysisErrorCode;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AiMealPersistenceService {
    private static final BigDecimal MIN_CONFIDENCE = BigDecimal.ZERO;
    private static final BigDecimal MAX_CONFIDENCE = BigDecimal.ONE;

    private final MealAnalysisPort mealAnalysisPort;

    @Transactional
    public AiMealAnalysisResult validateAndSave(
            AiMealAnalysisCommand command,
            MealNutritionAnalysisPort.AnalysisResult analysis) {
        // 외부 AI 응답을 신뢰하지 않고 DB 제약 및 API 계약에 맞는지 저장 직전에 다시 검증한다.
        validateAnalysis(analysis);

        MealAnalysis saved = mealAnalysisPort.save(MealAnalysis.create(
                command.userId(), command.mealType(), command.mealTime(), analysis.menu().trim(),
                analysis.kcal(), analysis.carbohydrate(), analysis.protein(), analysis.fat(), command.fileId()));

        // 평가·신뢰도·경고는 현재 식단 테이블에 컬럼이 없으므로 저장하지 않고 이번 응답에만 포함한다.
        return new AiMealAnalysisResult(
                saved.getId(), saved.getMealType(), saved.getMealTime(), saved.getMenu(), saved.getFileId(),
                saved.getKcal(), saved.getCarbohydrate(), saved.getProtein(), saved.getFat(),
                analysis.evaluation(), analysis.confidence(), analysis.warnings(),
                saved.getCreatedAt(), saved.getUpdatedAt());
    }

    private void validateAnalysis(MealNutritionAnalysisPort.AnalysisResult result) {
        if (result.menu() == null || result.menu().isBlank() || result.menu().trim().length() > 255
                || result.kcal() == null || result.kcal() < 0
                || !isValidNutrient(result.carbohydrate())
                || !isValidNutrient(result.protein())
                || !isValidNutrient(result.fat())
                || !isValidConfidence(result.confidence())) {
            throw new AiMealAnalysisException(MealAnalysisErrorCode.INVALID_AI_ANALYSIS_RESULT);
        }
    }

    private boolean isValidNutrient(BigDecimal value) {
        // migration의 DECIMAL(8,2)에 맞춰 정수부 최대 6자리와 소수부 최대 2자리를 허용한다.
        return value != null && value.signum() >= 0 && value.scale() <= 2 && value.precision() - value.scale() <= 6;
    }

    private boolean isValidConfidence(BigDecimal confidence) {
        return confidence != null
                && confidence.compareTo(MIN_CONFIDENCE) >= 0
                && confidence.compareTo(MAX_CONFIDENCE) <= 0;
    }
}
