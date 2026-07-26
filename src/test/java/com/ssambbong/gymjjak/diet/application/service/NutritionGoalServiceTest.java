package com.ssambbong.gymjjak.diet.application.service;

import com.ssambbong.gymjjak.diet.application.command.NutritionGoalCommand;
import com.ssambbong.gymjjak.diet.application.command.UpdateNutritionGoalCommand;
import com.ssambbong.gymjjak.diet.application.port.out.NutritionGoalPort;
import com.ssambbong.gymjjak.diet.domain.exception.DuplicateNutritionGoalException;
import com.ssambbong.gymjjak.diet.domain.model.NutritionGoal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NutritionGoalServiceTest {
    private NutritionGoalPort port;
    private NutritionGoalService service;

    @BeforeEach void setUp() { port = mock(NutritionGoalPort.class); service = new NutritionGoalService(port); }

    @Test void createsGoal() {
        when(port.existsByUserId(1L)).thenReturn(false);
        when(port.save(any())).thenAnswer(invocation -> NutritionGoal.builder().id(10L).userId(1L)
                .goalProtein(120L).goalCarbohydrate(250L).goalFat(60L).dailyGoalKcal(2000L).build());
        assertThat(service.create(new NutritionGoalCommand(1L, 120L, 250L, 60L, 2000L)).goalId()).isEqualTo(10L);
    }

    @Test void rejectsDuplicateGoal() {
        when(port.existsByUserId(1L)).thenReturn(true);
        assertThatThrownBy(() -> service.create(new NutritionGoalCommand(1L, 1L, 1L, 1L, 1L)))
                .isInstanceOf(DuplicateNutritionGoalException.class);
        verify(port, never()).save(any());
    }

    @Test void updatesOnlyPresentFields() {
        NutritionGoal goal = NutritionGoal.builder().id(10L).userId(1L).goalProtein(100L)
                .goalCarbohydrate(200L).goalFat(50L).dailyGoalKcal(1800L).build();
        when(port.findByUserId(1L)).thenReturn(Optional.of(goal));
        when(port.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        service.update(new UpdateNutritionGoalCommand(1L, 130L, true, null, false, null, false, null, false));
        ArgumentCaptor<NutritionGoal> captor = ArgumentCaptor.forClass(NutritionGoal.class);
        verify(port).save(captor.capture());
        assertThat(captor.getValue().getGoalProtein()).isEqualTo(130L);
        assertThat(captor.getValue().getGoalCarbohydrate()).isEqualTo(200L);
    }

    @Test void returnsEmptyWhenGoalDoesNotExist() {
        when(port.findByUserId(1L)).thenReturn(Optional.empty());
        assertThat(service.get(1L)).isEmpty();
    }
}
