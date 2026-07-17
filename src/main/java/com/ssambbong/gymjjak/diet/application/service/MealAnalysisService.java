package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.MealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.command.UpdateMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.in.MealAnalysisUseCase;
import com.ssambbong.gymjjak.diet.application.port.out.MealAnalysisPort;
import com.ssambbong.gymjjak.diet.application.query.MealPageQuery;
import com.ssambbong.gymjjak.diet.application.result.MealAnalysisResult;
import com.ssambbong.gymjjak.diet.application.result.MealPageResult;
import com.ssambbong.gymjjak.diet.domain.exception.MealAnalysisNotFoundException;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MealAnalysisService implements MealAnalysisUseCase {

    private final MealAnalysisPort mealAnalysisPort;

    @Override
    @Transactional
    public MealAnalysisResult create(MealAnalysisCommand command) {
        MealAnalysis meal = MealAnalysis.create(command.userId(), command.mealType(), command.mealTime(),
                command.menu(), command.kcal(), command.fileId());
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
        MealAnalysis meal = getOwnedMeal(command.userId(), mealId);
        meal.update(
                command.mealType(), command.mealTypePresent(),
                command.mealTime(), command.mealTimePresent(),
                command.menu(), command.menuPresent(),
                command.kcal(), command.kcalPresent(),
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
}
