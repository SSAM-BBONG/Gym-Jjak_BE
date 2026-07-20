package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.AiMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.in.AiMealAnalysisUseCase;
import com.ssambbong.gymjjak.diet.application.port.out.*;
import com.ssambbong.gymjjak.diet.application.result.AiMealAnalysisResult;
import com.ssambbong.gymjjak.diet.application.result.MealNutritionSummary;
import com.ssambbong.gymjjak.diet.domain.exception.AiNutritionAccessRequiredException;
import com.ssambbong.gymjjak.diet.domain.model.NutritionGoal;
import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiMealAnalysisService implements AiMealAnalysisUseCase {
    private final AiNutritionAccessPort aiNutritionAccessPort;
    private final AiMealImagePort aiMealImagePort;
    private final NutritionGoalPort nutritionGoalPort;
    private final MealAnalysisPort mealAnalysisPort;
    private final MealNutritionAnalysisPort mealNutritionAnalysisPort;
    private final AiMealPersistenceService persistenceService;
    private final FileUseCase fileUseCase;

    @Override
    public AiMealAnalysisResult analyze(AiMealAnalysisCommand command) {
        // 미구독 사용자의 파일 레코드가 생성되지 않도록 가장 먼저 AI 사용 권한을 확인한다.
        validateAccess(command.userId());

        // 프론트가 S3에 업로드한 식단 이미지 메타데이터를 등록하고 영속 식별자인 fileId를 발급한다.
        FileRegistrationResult registeredFile = fileUseCase.registerFiles(List.of(new CreateFileCommand(
                command.userId(), command.fileKey(), command.originalName(), command.contentType(),
                command.fileSize(), FileType.MEAL_IMAGE))).get(0);
        Long fileId = registeredFile.fileId();

        // 등록된 파일의 소유권과 식단 이미지 유형을 다시 확인한 뒤 AI 서버용 임시 조회 URL을 만든다.
        String imageUrl = aiMealImagePort.resolveAccessibleImageUrl(fileId, command.userId());

        LocalDate mealDate = command.mealTime().toLocalDate();
        LocalDateTime startOfDay = mealDate.atStartOfDay();
        MealNutritionSummary todayIntake = mealAnalysisPort.sumNutritionByUserIdAndMealTimeBetween(
                command.userId(), startOfDay, startOfDay.plusDays(1));

        // 영양 목표가 없어도 이미지 분석은 수행하며, 목표 대비 평가는 AI 서버가 생략할 수 있다.
        MealNutritionAnalysisPort.NutritionGoalSnapshot goal = nutritionGoalPort.findByUserId(command.userId())
                .map(this::toGoalSnapshot)
                .orElse(null);

        // AI 서버 응답을 동기 방식으로 기다린 뒤, 검증과 JPA 저장까지 같은 요청 흐름에서 순차 처리한다.
        MealNutritionAnalysisPort.AnalysisResult analysis = mealNutritionAnalysisPort.analyze(
                new MealNutritionAnalysisPort.AnalysisRequest(
                        imageUrl, command.mealType().name(), command.mealTime(), goal, todayIntake));
        return persistenceService.validateAndSave(command, fileId, analysis);
    }

    private void validateAccess(Long userId) {
        if (!aiNutritionAccessPort.hasActiveAccess(userId)) {
            throw new AiNutritionAccessRequiredException();
        }
    }

    private MealNutritionAnalysisPort.NutritionGoalSnapshot toGoalSnapshot(NutritionGoal goal) {
        return new MealNutritionAnalysisPort.NutritionGoalSnapshot(
                goal.getGoalProtein(), goal.getGoalCarbohydrate(), goal.getGoalFat(), goal.getDailyGoalKcal());
    }

}
