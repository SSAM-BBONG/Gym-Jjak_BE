package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.MealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.command.UpdateMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.in.MealAnalysisUseCase;
import com.ssambbong.gymjjak.diet.application.port.out.MealAnalysisPort;
import com.ssambbong.gymjjak.diet.application.port.out.AiNutritionAccessPort;
import com.ssambbong.gymjjak.diet.application.query.MealPageQuery;
import com.ssambbong.gymjjak.diet.application.result.MealAnalysisResult;
import com.ssambbong.gymjjak.diet.application.result.MealPageResult;
import com.ssambbong.gymjjak.diet.domain.exception.MealAnalysisNotFoundException;
import com.ssambbong.gymjjak.diet.domain.exception.AiNutritionAccessRequiredException;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MealAnalysisService implements MealAnalysisUseCase {

    private final MealAnalysisPort mealAnalysisPort;
    private final AiNutritionAccessPort aiNutritionAccessPort;

    @Override
    @Transactional
    public MealAnalysisResult create(MealAnalysisCommand command) {
        // 기존 무료 식단 등록은 허용하고, 영양성분 저장 요청에만 활성 AI 구독을 요구한다.
        if (command.hasMacronutrients()) {
            validateAiNutritionAccess(command.userId());
        }
        MealAnalysis meal = MealAnalysis.create(command.userId(), command.mealType(), command.mealTime(),
                command.menu(), command.kcal(), command.carbohydrate(), command.protein(), command.fat(), command.fileId());
        return MealAnalysisResult.from(mealAnalysisPort.save(meal));
    }

    @Override
    @Transactional(readOnly = true)
    public MealAnalysisResult get(Long userId, Long mealId) {
        return MealAnalysisResult.from(getOwnedMeal(userId, mealId));
    }

    @Transactional(readOnly = true)
    @Override
    public MealPageResult<MealAnalysisResult> getList(MealPageQuery query) {
        return mealAnalysisPort.findAllByUserId(query).map(MealAnalysisResult::from);
    }

    @Transactional
    @Override
    public MealAnalysisResult update(Long mealId, UpdateMealAnalysisCommand command) {
        // 소유권을 먼저 확인해 다른 사용자의 식단 존재 여부가 권한 검사로 노출되지 않게 한다.
        MealAnalysis meal = getOwnedMeal(command.userId(), mealId);
        if (command.updatesMacronutrients()) {
            validateAiNutritionAccess(command.userId());
        }
        meal.update(
                command.mealType(), command.mealTypePresent(),
                command.mealTime(), command.mealTimePresent(),
                command.menu(), command.menuPresent(),
                command.kcal(), command.kcalPresent(),
                command.carbohydrate(), command.carbohydratePresent(),
                command.protein(), command.proteinPresent(),
                command.fat(), command.fatPresent(),
                command.fileId(), command.fileIdPresent()
        );
        return MealAnalysisResult.from(mealAnalysisPort.save(meal));
    }

    @Transactional
    @Override
    public void delete(Long userId, Long mealId) {
        int deletedCount = mealAnalysisPort.deleteByIdAndUserId(mealId, userId);
        if (deletedCount == 0) {
            throw new MealAnalysisNotFoundException(mealId);
        }
    }

    private MealAnalysis getOwnedMeal(Long userId, Long mealId) {
        // 소유자 조건을 조회에 포함해 다른 사용자의 식단 존재 여부도 노출하지 않는다.
        return mealAnalysisPort.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new MealAnalysisNotFoundException(mealId));
    }

    private void validateAiNutritionAccess(Long userId) {
        // 결제 이력이 아니라 현재 시점에 유효한 활성 구독을 기준으로 판단한다.
        if (!aiNutritionAccessPort.hasActiveAccess(userId)) {
            throw new AiNutritionAccessRequiredException();
        }
    }
}
