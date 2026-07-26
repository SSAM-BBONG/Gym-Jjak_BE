package com.ssambbong.gymjjak.trainer.routinerecommendation.application.service;

import com.ssambbong.gymjjak.calendar.application.port.out.CalendarPtReservationPort;
import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPort;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiaryResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayDiarySetResult;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.command.RecommendTrainerRoutineCommand;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.model.TrainerRoutineGender;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.model.TrainerRoutineGoal;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.out.TrainerRoutineAiPort;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.result.TrainerRoutineRecommendationResult;
import com.ssambbong.gymjjak.trainer.routinerecommendation.domain.exception.TrainerRoutineRecommendationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerRoutineRecommendationServiceTest {
    @Mock private CalendarPtReservationPort calendarPtReservationPort;
    @Mock private WorkoutDiaryPort workoutDiaryPort;
    @Mock private TrainerRoutineAiPort trainerRoutineAiPort;
    @InjectMocks private TrainerRoutineRecommendationService service;

    @Test
    void recommend_sendsRecentMemberWorkoutsOnlyAfterActivePtRelationIsVerified() {
        when(calendarPtReservationPort.existsActivePtRelationWithTrainer(10L, 20L)).thenReturn(true);
        when(workoutDiaryPort.findDiariesByUserIdAndPeriod(eq(10L), any(), any())).thenReturn(List.of(diary()));
        when(trainerRoutineAiPort.recommend(any(), anyList())).thenReturn(result());

        TrainerRoutineRecommendationResult actual = service.recommend(command());

        ArgumentCaptor<List<TrainerRoutineAiPort.TrainerWorkoutSnapshot>> workouts = ArgumentCaptor.forClass(List.class);
        verify(trainerRoutineAiPort).recommend(eq(command()), workouts.capture());
        assertThat(actual.title()).isEqualTo("recommended");
        assertThat(workouts.getValue()).singleElement().satisfies(workout -> {
            assertThat(workout.exercise()).isEqualTo("Bench Press");
            assertThat(workout.sets()).singleElement().satisfies(set -> assertThat(set.reps()).isEqualTo(10));
        });
    }

    @Test
    void recommend_rejectsMemberWithoutActivePtRelationBeforeReadingWorkoutHistory() {
        when(calendarPtReservationPort.existsActivePtRelationWithTrainer(10L, 20L)).thenReturn(false);

        assertThatThrownBy(() -> service.recommend(command()))
                .isInstanceOf(TrainerRoutineRecommendationException.class);

        verifyNoInteractions(workoutDiaryPort, trainerRoutineAiPort);
    }

    private RecommendTrainerRoutineCommand command() {
        return new RecommendTrainerRoutineCommand(20L, 10L, TrainerRoutineGender.MALE, 28,
                new BigDecimal("175.5"), new BigDecimal("72.3"), TrainerRoutineGoal.MUSCLE_GAIN);
    }

    private CalendarDayDiaryResult diary() {
        return new CalendarDayDiaryResult(1L, 2L, "Bench Press", LocalDate.now(), PartType.CHEST,
                List.of(new CalendarDayDiarySetResult(1L, 1, new BigDecimal("60"), 10)));
    }

    private TrainerRoutineRecommendationResult result() {
        return new TrainerRoutineRecommendationResult("COMPLETE", "recommended", "summary", List.of(), List.of(), List.of(), List.of());
    }
}
