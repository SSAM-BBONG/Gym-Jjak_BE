package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.MealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.command.UpdateMealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.out.MealAnalysisPort;
import com.ssambbong.gymjjak.diet.application.port.out.AiNutritionAccessPort;
import com.ssambbong.gymjjak.diet.application.port.out.MealAccessPort;
import com.ssambbong.gymjjak.diet.application.query.MealPageQuery;
import com.ssambbong.gymjjak.diet.application.result.MealPageResult;
import com.ssambbong.gymjjak.diet.application.result.MealAnalysisResult;
import com.ssambbong.gymjjak.diet.domain.exception.MealAnalysisNotFoundException;
import com.ssambbong.gymjjak.diet.domain.exception.AiNutritionAccessRequiredException;
import com.ssambbong.gymjjak.diet.domain.exception.MealAccessDeniedException;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import com.ssambbong.gymjjak.diet.domain.model.MealType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MealAnalysisServiceTest {

    @Mock
    private MealAnalysisPort repository;

    @Mock
    private AiNutritionAccessPort aiNutritionAccessPort;

    @Mock
    private MealAccessPort mealAccessPort;

    @InjectMocks
    private MealAnalysisService service;

    @Test
    void 식단을_등록한다() {
        LocalDateTime mealTime = LocalDateTime.of(2026, 7, 18, 8, 30);
        MealAnalysis saved = meal(1L, 10L, MealType.BREAKFAST, mealTime, "계란", 150L, null);
        given(repository.save(any(MealAnalysis.class))).willReturn(saved);

        MealAnalysisResult result = service.create(
                new MealAnalysisCommand(10L, MealType.BREAKFAST, mealTime, "계란", 150L,
                        null, null, null, null));

        assertThat(result.mealId()).isEqualTo(1L);
        assertThat(result.mealType()).isEqualTo(MealType.BREAKFAST);
        verify(repository).save(any(MealAnalysis.class));
    }

    @Test
    void 본인_소유_식단을_수정한다() {
        LocalDateTime oldTime = LocalDateTime.of(2026, 7, 18, 8, 30);
        LocalDateTime newTime = LocalDateTime.of(2026, 7, 18, 12, 30);
        MealAnalysis ownedMeal = meal(1L, 10L, MealType.BREAKFAST, oldTime, "계란", 150L, null);
        given(repository.findByIdAndUserId(1L, 10L)).willReturn(Optional.of(ownedMeal));
        given(repository.save(ownedMeal)).willReturn(ownedMeal);

        MealAnalysisResult result = service.update(1L,
                new UpdateMealAnalysisCommand(
                        10L,
                        MealType.LUNCH, true,
                        newTime, true,
                        "샐러드", true,
                        300L, true,
                        null, false,
                        null, false,
                        null, false,
                        20L, true
                ));

        assertThat(result.mealType()).isEqualTo(MealType.LUNCH);
        assertThat(result.menu()).isEqualTo("샐러드");
        assertThat(result.fileId()).isEqualTo(20L);
    }

    @Test
    void 본인_소유가_아닌_식단은_조회할_수_없다() {
        given(repository.findByIdAndUserId(1L, 10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(10L, 10L, 1L))
                .isInstanceOf(MealAnalysisNotFoundException.class);
    }

    @Test
    void 담당_트레이너는_회원의_식단_상세를_조회할_수_있다() {
        MealAnalysis memberMeal = meal(1L, 20L, MealType.LUNCH,
                LocalDateTime.of(2026, 7, 21, 12, 30), "샐러드", 300L, null);
        given(mealAccessPort.existsActivePtRelation(20L, 10L)).willReturn(true);
        given(repository.findByIdAndUserId(1L, 20L)).willReturn(Optional.of(memberMeal));

        MealAnalysisResult result = service.get(10L, 20L, 1L);

        assertThat(result.mealId()).isEqualTo(1L);
        verify(mealAccessPort).existsActivePtRelation(20L, 10L);
    }

    @Test
    void 담당_PT_관계가_없으면_다른_회원의_식단을_조회할_수_없다() {
        given(mealAccessPort.existsActivePtRelation(20L, 10L)).willReturn(false);

        assertThatThrownBy(() -> service.get(10L, 20L, 1L))
                .isInstanceOf(MealAccessDeniedException.class);
    }

    @Test
    void 담당_트레이너는_회원의_식단_목록을_조회할_수_있다() {
        MealPageQuery query = new MealPageQuery(10L, 20L, 0, 20, null);
        given(mealAccessPort.existsActivePtRelation(20L, 10L)).willReturn(true);
        given(repository.findAllByUserId(query))
                .willReturn(new MealPageResult<>(List.of(), 0, 20, 0, 0, false));

        service.getList(query);

        verify(repository).findAllByUserId(query);
    }

    @Test
    void 본인_소유_식단을_삭제한다() {
        given(repository.deleteByIdAndUserId(1L, 10L)).willReturn(1);

        service.delete(10L, 1L);

        verify(repository).deleteByIdAndUserId(1L, 10L);
    }

    @Test
    void 활성_AI_구독자는_영양성분을_포함해_등록한다() {
        LocalDateTime mealTime = LocalDateTime.of(2026, 7, 18, 8, 30);
        BigDecimal carbohydrate = new BigDecimal("67.00");
        BigDecimal protein = new BigDecimal("51.90");
        BigDecimal fat = new BigDecimal("7.60");
        given(aiNutritionAccessPort.hasActiveAccess(10L)).willReturn(true);
        given(repository.save(any(MealAnalysis.class))).willAnswer(invocation -> invocation.getArgument(0));

        MealAnalysisResult result = service.create(new MealAnalysisCommand(
                10L, MealType.LUNCH, mealTime, "닭가슴살", 554L,
                carbohydrate, protein, fat, null));

        assertThat(result.carbohydrate()).isEqualByComparingTo("67.00");
        assertThat(result.protein()).isEqualByComparingTo("51.90");
        assertThat(result.fat()).isEqualByComparingTo("7.60");
    }

    @Test
    void 미구독자는_영양성분을_등록할_수_없다() {
        LocalDateTime mealTime = LocalDateTime.of(2026, 7, 18, 8, 30);
        given(aiNutritionAccessPort.hasActiveAccess(10L)).willReturn(false);

        assertThatThrownBy(() -> service.create(new MealAnalysisCommand(
                10L, MealType.LUNCH, mealTime, "닭가슴살", 554L,
                new BigDecimal("67.00"), null, null, null)))
                .isInstanceOf(AiNutritionAccessRequiredException.class);
    }

    @Test
    void 활성_AI_구독자는_영양성분을_null로_제거할_수_있다() {
        LocalDateTime mealTime = LocalDateTime.of(2026, 7, 18, 8, 30);
        MealAnalysis ownedMeal = MealAnalysis.builder().id(1L).userId(10L).mealType(MealType.LUNCH)
                .mealTime(mealTime).menu("닭가슴살").kcal(554L)
                .protein(new BigDecimal("51.90")).build();
        given(repository.findByIdAndUserId(1L, 10L)).willReturn(Optional.of(ownedMeal));
        given(aiNutritionAccessPort.hasActiveAccess(10L)).willReturn(true);
        given(repository.save(ownedMeal)).willReturn(ownedMeal);

        MealAnalysisResult result = service.update(1L, new UpdateMealAnalysisCommand(
                10L, null, false, null, false, null, false, null, false,
                null, false, null, true, null, false, null, false));

        assertThat(result.protein()).isNull();
    }

    @Test
    void 미구독자는_영양성분을_null로_제거할_수_없다() {
        LocalDateTime mealTime = LocalDateTime.of(2026, 7, 18, 8, 30);
        MealAnalysis ownedMeal = MealAnalysis.builder().id(1L).userId(10L).mealType(MealType.LUNCH)
                .mealTime(mealTime).menu("닭가슴살").protein(new BigDecimal("51.90")).build();
        given(repository.findByIdAndUserId(1L, 10L)).willReturn(Optional.of(ownedMeal));
        given(aiNutritionAccessPort.hasActiveAccess(10L)).willReturn(false);

        assertThatThrownBy(() -> service.update(1L, new UpdateMealAnalysisCommand(
                10L, null, false, null, false, null, false, null, false,
                null, false, null, true, null, false, null, false)))
                .isInstanceOf(AiNutritionAccessRequiredException.class);
    }

    private MealAnalysis meal(Long id, Long userId, MealType mealType, LocalDateTime mealTime,
                              String menu, Long kcal, Long fileId) {
        return MealAnalysis.builder()
                .id(id)
                .userId(userId)
                .mealType(mealType)
                .mealTime(mealTime)
                .menu(menu)
                .kcal(kcal)
                .fileId(fileId)
                .createdAt(LocalDateTime.of(2026, 7, 18, 8, 0))
                .updatedAt(LocalDateTime.of(2026, 7, 18, 8, 0))
                .build();
    }
}
