package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.MealAnalysisCommand;
import com.ssambbong.gymjjak.diet.application.port.out.MealAnalysisPort;
import com.ssambbong.gymjjak.diet.application.result.MealAnalysisResult;
import com.ssambbong.gymjjak.diet.domain.exception.MealAnalysisNotFoundException;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import com.ssambbong.gymjjak.diet.domain.model.MealType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MealAnalysisServiceTest {

    @Mock
    private MealAnalysisPort repository;

    @InjectMocks
    private MealAnalysisService service;

    @Test
    void 식단을_등록한다() {
        LocalDateTime mealTime = LocalDateTime.of(2026, 7, 18, 8, 30);
        MealAnalysis saved = meal(1L, 10L, MealType.BREAKFAST, mealTime, "계란", 150L, null);
        given(repository.save(any(MealAnalysis.class))).willReturn(saved);

        MealAnalysisResult result = service.create(
                new MealAnalysisCommand(10L, MealType.BREAKFAST, mealTime, "계란", 150L, null));

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
                new MealAnalysisCommand(10L, MealType.LUNCH, newTime, "샐러드", 300L, 20L));

        assertThat(result.mealType()).isEqualTo(MealType.LUNCH);
        assertThat(result.menu()).isEqualTo("샐러드");
        assertThat(result.fileId()).isEqualTo(20L);
    }

    @Test
    void 본인_소유가_아닌_식단은_조회할_수_없다() {
        given(repository.findByIdAndUserId(1L, 10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(10L, 1L))
                .isInstanceOf(MealAnalysisNotFoundException.class);
    }

    @Test
    void 본인_소유_식단을_삭제한다() {
        MealAnalysis ownedMeal = meal(1L, 10L, MealType.SNACK,
                LocalDateTime.of(2026, 7, 18, 15, 0), "견과류", null, null);
        given(repository.findByIdAndUserId(1L, 10L)).willReturn(Optional.of(ownedMeal));

        service.delete(10L, 1L);

        verify(repository).deleteById(1L);
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
