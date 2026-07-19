package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.AiMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.out.*;
import com.ssambbong.gymjjak.diet.application.result.AiMealAnalysisResult;
import com.ssambbong.gymjjak.diet.application.result.MealNutritionSummary;
import com.ssambbong.gymjjak.diet.domain.exception.AiMealAnalysisException;
import com.ssambbong.gymjjak.diet.domain.exception.AiNutritionAccessRequiredException;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import com.ssambbong.gymjjak.diet.domain.model.MealType;
import com.ssambbong.gymjjak.diet.domain.model.NutritionGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class AiMealAnalysisServiceTest {
    @Mock private AiNutritionAccessPort accessPort;
    @Mock private AiMealImagePort imagePort;
    @Mock private NutritionGoalPort nutritionGoalPort;
    @Mock private MealAnalysisPort mealAnalysisPort;
    @Mock private MealNutritionAnalysisPort analysisPort;
    @Mock private AiMealPersistenceService persistenceService;
    @InjectMocks private AiMealAnalysisService service;

    @Test
    void 활성_구독자의_이미지를_분석하고_식단으로_저장한다() {
        LocalDateTime mealTime = LocalDateTime.of(2026, 7, 18, 12, 30);
        given(accessPort.hasActiveAccess(10L)).willReturn(true);
        given(imagePort.resolveAccessibleImageUrl(15L, 10L)).willReturn("https://example.com/meal.jpg");
        given(nutritionGoalPort.findByUserId(10L)).willReturn(Optional.of(NutritionGoal.builder()
                .id(1L).userId(10L).goalProtein(120L).goalCarbohydrate(250L)
                .goalFat(60L).dailyGoalKcal(2000L).build()));
        given(mealAnalysisPort.sumNutritionByUserIdAndMealTimeBetween(any(), any(), any()))
                .willReturn(new MealNutritionSummary(930L, new BigDecimal("105.50"),
                        new BigDecimal("42.30"), new BigDecimal("21.20")));
        MealNutritionAnalysisPort.AnalysisResult analysis = new MealNutritionAnalysisPort.AnalysisResult(
                "닭가슴살 샐러드", 554L, new BigDecimal("67.00"), new BigDecimal("51.90"),
                new BigDecimal("7.60"), "단백질 섭취에 적합합니다.", new BigDecimal("0.92"),
                List.of("드레싱 양에 따라 달라질 수 있습니다."));
        given(analysisPort.analyze(any())).willReturn(Mono.just(analysis));
        given(persistenceService.validateAndSave(any(), any())).willReturn(new AiMealAnalysisResult(
                1L, MealType.LUNCH, mealTime, "닭가슴살 샐러드", 15L, 554L,
                new BigDecimal("67.00"), new BigDecimal("51.90"), new BigDecimal("7.60"),
                "단백질 섭취에 적합합니다.", new BigDecimal("0.92"), List.of(), mealTime, mealTime));

        AiMealAnalysisResult result = service.analyze(
                new AiMealAnalysisCommand(10L, 15L, MealType.LUNCH, mealTime)).block();

        assertThat(result.menu()).isEqualTo("닭가슴살 샐러드");
        assertThat(result.protein()).isEqualByComparingTo("51.90");
        assertThat(result.confidence()).isEqualByComparingTo("0.92");

        ArgumentCaptor<MealNutritionAnalysisPort.AnalysisRequest> requestCaptor =
                ArgumentCaptor.forClass(MealNutritionAnalysisPort.AnalysisRequest.class);
        verify(analysisPort).analyze(requestCaptor.capture());
        assertThat(requestCaptor.getValue().nutritionGoal().kcal()).isEqualTo(2000L);
        assertThat(requestCaptor.getValue().todayIntake().kcal()).isEqualTo(930L);
        verify(mealAnalysisPort).sumNutritionByUserIdAndMealTimeBetween(
                10L, LocalDateTime.of(2026, 7, 18, 0, 0), LocalDateTime.of(2026, 7, 19, 0, 0));
    }

    @Test
    void 영양_목표가_없어도_AI_분석을_수행한다() {
        LocalDateTime mealTime = LocalDateTime.of(2026, 7, 18, 12, 30);
        given(accessPort.hasActiveAccess(10L)).willReturn(true);
        given(imagePort.resolveAccessibleImageUrl(15L, 10L)).willReturn("https://example.com/meal.jpg");
        given(nutritionGoalPort.findByUserId(10L)).willReturn(Optional.empty());
        given(mealAnalysisPort.sumNutritionByUserIdAndMealTimeBetween(any(), any(), any()))
                .willReturn(MealNutritionSummary.empty());
        MealNutritionAnalysisPort.AnalysisResult analysis = new MealNutritionAnalysisPort.AnalysisResult(
                "사과", 95L, new BigDecimal("25.00"), new BigDecimal("0.50"),
                new BigDecimal("0.30"), null, BigDecimal.ONE, List.of());
        given(analysisPort.analyze(any())).willReturn(Mono.just(analysis));
        given(persistenceService.validateAndSave(any(), any())).willReturn(new AiMealAnalysisResult(
                1L, MealType.SNACK, mealTime, "사과", 15L, 95L, new BigDecimal("25.00"),
                new BigDecimal("0.50"), new BigDecimal("0.30"), null, BigDecimal.ONE,
                List.of(), mealTime, mealTime));

        service.analyze(new AiMealAnalysisCommand(10L, 15L, MealType.SNACK, mealTime)).block();

        ArgumentCaptor<MealNutritionAnalysisPort.AnalysisRequest> captor =
                ArgumentCaptor.forClass(MealNutritionAnalysisPort.AnalysisRequest.class);
        verify(analysisPort).analyze(captor.capture());
        assertThat(captor.getValue().nutritionGoal()).isNull();
    }

    @Test
    void 미구독자는_AI_분석을_사용할_수_없다() {
        given(accessPort.hasActiveAccess(10L)).willReturn(false);

        assertThatThrownBy(() -> service.analyze(new AiMealAnalysisCommand(
                10L, 15L, MealType.LUNCH, LocalDateTime.of(2026, 7, 18, 12, 30))))
                .isInstanceOf(AiNutritionAccessRequiredException.class);
        verify(imagePort, never()).resolveAccessibleImageUrl(any(), any());
    }

    @Test
    void 유효하지_않은_AI_응답은_저장하지_않는다() {
        LocalDateTime mealTime = LocalDateTime.of(2026, 7, 18, 12, 30);
        AiMealPersistenceService realPersistenceService = new AiMealPersistenceService(mealAnalysisPort);
        MealNutritionAnalysisPort.AnalysisResult invalidAnalysis = new MealNutritionAnalysisPort.AnalysisResult(
                "", 100L, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                null, new BigDecimal("0.5"), List.of());

        assertThatThrownBy(() -> realPersistenceService.validateAndSave(
                new AiMealAnalysisCommand(10L, 15L, MealType.LUNCH, mealTime), invalidAnalysis))
                .isInstanceOf(AiMealAnalysisException.class);
        verify(mealAnalysisPort, never()).save(any());
    }
}
