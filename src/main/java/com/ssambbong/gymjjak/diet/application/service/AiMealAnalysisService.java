package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.AiMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.in.AiMealAnalysisUseCase;
import com.ssambbong.gymjjak.diet.application.port.out.*;
import com.ssambbong.gymjjak.diet.application.result.AiMealAnalysisResult;
import com.ssambbong.gymjjak.diet.application.result.MealNutritionSummary;
import com.ssambbong.gymjjak.diet.domain.exception.AiNutritionAccessRequiredException;
import com.ssambbong.gymjjak.diet.domain.model.NutritionGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class AiMealAnalysisService implements AiMealAnalysisUseCase {
    private final AiNutritionAccessPort aiNutritionAccessPort;
    private final AiMealImagePort aiMealImagePort;
    private final NutritionGoalPort nutritionGoalPort;
    private final MealAnalysisPort mealAnalysisPort;
    private final MealNutritionAnalysisPort mealNutritionAnalysisPort;
    private final AiMealPersistenceService persistenceService;

    @Override
    public Mono<AiMealAnalysisResult> analyze(AiMealAnalysisCommand command) {
        // 아래 사전 조회는 기존 JPA 동기 방식을 유지하며 컨트롤러 요청 스레드에서 순서대로 수행한다.
        validateAccess(command.userId());
        String imageUrl = aiMealImagePort.resolveAccessibleImageUrl(command.fileId(), command.userId());

        LocalDate mealDate = command.mealTime().toLocalDate();
        LocalDateTime startOfDay = mealDate.atStartOfDay();
        MealNutritionSummary todayIntake = mealAnalysisPort.sumNutritionByUserIdAndMealTimeBetween(
                command.userId(), startOfDay, startOfDay.plusDays(1));

        // 영양 목표가 없어도 이미지 분석은 수행하며, 목표 대비 평가는 AI 서버가 생략할 수 있다.
        MealNutritionAnalysisPort.NutritionGoalSnapshot goal = nutritionGoalPort.findByUserId(command.userId())
                .map(this::toGoalSnapshot)
                .orElse(null);

        // AI 네트워크 호출만 WebClient로 논블로킹 처리하며 이 구간에는 DB 트랜잭션이 존재하지 않는다.
        return mealNutritionAnalysisPort.analyze(new MealNutritionAnalysisPort.AnalysisRequest(
                        imageUrl, command.mealType().name(), command.mealTime(), goal, todayIntake))
                .flatMap(analysis -> Mono.fromCallable(() -> persistenceService.validateAndSave(command, analysis))
                        // JPA 저장은 블로킹 작업이므로 WebClient 이벤트 루프가 아닌 전용 탄력 스레드에서 실행한다.
                        .subscribeOn(Schedulers.boundedElastic()));
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
