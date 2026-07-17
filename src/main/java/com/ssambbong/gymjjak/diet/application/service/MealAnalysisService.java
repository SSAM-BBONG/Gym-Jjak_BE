package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.MealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.in.MealAnalysisUseCase;
import com.ssambbong.gymjjak.diet.application.port.out.MealAnalysisPort;
import com.ssambbong.gymjjak.diet.application.result.MealAnalysisResult;
import com.ssambbong.gymjjak.diet.domain.exception.MealAnalysisNotFoundException;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<MealAnalysisResult> getList(Long userId, Pageable pageable) {
        return mealAnalysisPort.findAllByUserId(userId, pageable).map(MealAnalysisResult::from);
    }

    @Transactional
    @Override
    public MealAnalysisResult update(Long mealId, MealAnalysisCommand command) {
        MealAnalysis meal = getOwnedMeal(command.userId(), mealId);
        meal.update(command.mealType(), command.mealTime(), command.menu(), command.kcal(), command.fileId());
        return MealAnalysisResult.from(mealAnalysisPort.save(meal));
    }

    @Transactional
    @Override
    public void delete(Long userId, Long mealId) {
        MealAnalysis meal = getOwnedMeal(userId, mealId);
        mealAnalysisPort.deleteById(meal.getId());
    }

    private MealAnalysis getOwnedMeal(Long userId, Long mealId) {
        // 소유자 조건을 조회에 포함해 다른 사용자의 식단 존재 여부도 노출하지 않는다.
        return mealAnalysisPort.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new MealAnalysisNotFoundException(mealId));
    }
}
