package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.MealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.command.MealImageMetadataCommand;
import com.ssambbong.gymjjak.diet.application.command.UpdateMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.in.MealAnalysisUseCase;
import com.ssambbong.gymjjak.diet.application.port.out.MealAnalysisPort;
import com.ssambbong.gymjjak.diet.application.port.out.AiNutritionAccessPort;
import com.ssambbong.gymjjak.diet.application.port.out.MealAccessPort;
import com.ssambbong.gymjjak.diet.application.port.out.MealImageUrlPort;
import com.ssambbong.gymjjak.diet.application.query.MealPageQuery;
import com.ssambbong.gymjjak.diet.application.result.MealAnalysisResult;
import com.ssambbong.gymjjak.diet.application.result.MealAnalysisDetailResult;
import com.ssambbong.gymjjak.diet.application.result.MealPageResult;
import com.ssambbong.gymjjak.diet.domain.exception.MealAnalysisNotFoundException;
import com.ssambbong.gymjjak.diet.domain.exception.MealAccessDeniedException;
import com.ssambbong.gymjjak.diet.domain.exception.AiNutritionAccessRequiredException;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MealAnalysisService implements MealAnalysisUseCase {

    private final MealAnalysisPort mealAnalysisPort;
    private final AiNutritionAccessPort aiNutritionAccessPort;
    private final MealAccessPort mealAccessPort;
    private final FileUseCase fileUseCase;
    private final MealImageUrlPort mealImageUrlPort;

    @Override
    @Transactional
    public MealAnalysisResult create(MealAnalysisCommand command) {
        // 기존 무료 식단 등록은 허용하고, 영양성분 저장 요청에만 활성 AI 구독을 요구한다.
        if (command.hasMacronutrients()) {
            validateAiNutritionAccess(command.userId());
        }
        Long fileId = registerMealImage(command);
        MealAnalysis meal = MealAnalysis.create(command.userId(), command.mealType(), command.mealTime(),
                command.menu(), command.kcal(), command.carbohydrate(), command.protein(), command.fat(), fileId);
        return MealAnalysisResult.from(mealAnalysisPort.save(meal));
    }

    @Override
    @Transactional(readOnly = true)
    public MealAnalysisDetailResult get(Long requesterUserId, Long targetUserId, Long mealId) {
        validateReadAccess(requesterUserId, targetUserId);
        MealAnalysis meal = getOwnedMeal(targetUserId, mealId);
        String imageUrl = meal.getFileId() == null
                ? null
                : mealImageUrlPort.resolve(meal.getFileId(), targetUserId);
        return new MealAnalysisDetailResult(MealAnalysisResult.from(meal), imageUrl);
    }

    @Transactional(readOnly = true)
    @Override
    public MealPageResult<MealAnalysisResult> getList(MealPageQuery query) {
        validateReadAccess(query.requesterUserId(), query.targetUserId());
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
        Long fileId = command.filePresent() ? registerMealImage(command.userId(), command.file()) : null;
        meal.update(
                command.mealType(), command.mealTypePresent(),
                command.mealTime(), command.mealTimePresent(),
                command.menu(), command.menuPresent(),
                command.kcal(), command.kcalPresent(),
                command.carbohydrate(), command.carbohydratePresent(),
                command.protein(), command.proteinPresent(),
                command.fat(), command.fatPresent(),
                fileId, command.filePresent()
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

    private void validateReadAccess(Long requesterUserId, Long targetUserId) {
        if (requesterUserId.equals(targetUserId)) {
            return;
        }
        if (!mealAccessPort.existsActivePtRelation(targetUserId, requesterUserId)) {
            throw new MealAccessDeniedException();
        }
    }

    private void validateAiNutritionAccess(Long userId) {
        // 결제 이력이 아니라 현재 시점에 유효한 활성 구독을 기준으로 판단한다.
        if (!aiNutritionAccessPort.hasActiveAccess(userId)) {
            throw new AiNutritionAccessRequiredException();
        }
    }

    private Long registerMealImage(MealAnalysisCommand command) {
        return registerMealImage(command.userId(), command.file());
    }

    private Long registerMealImage(Long userId, MealImageMetadataCommand file) {
        if (file == null) return null;
        FileRegistrationResult registeredFile = fileUseCase.registerFiles(List.of(new CreateFileCommand(
                userId, file.fileKey(), file.originalName(), file.contentType(), file.fileSize(), FileType.MEAL_IMAGE
        ))).get(0);
        return registeredFile.fileId();
    }
}
