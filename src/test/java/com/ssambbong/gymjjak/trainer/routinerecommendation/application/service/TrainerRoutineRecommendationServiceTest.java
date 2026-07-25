package com.ssambbong.gymjjak.trainer.routinerecommendation.application.service;

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
    @Mock private TrainerRoutineRecommendationSnapshotService snapshotService;
    @Mock private TrainerRoutineAiPort trainerRoutineAiPort;
    @InjectMocks private TrainerRoutineRecommendationService service;

    @Test
    void recommend_sendsRecentMemberWorkoutsOnlyAfterActivePtRelationIsVerified() {
        when(snapshotService.load(command())).thenReturn(workoutData());
        when(trainerRoutineAiPort.recommend(any(), any())).thenReturn(result());

        TrainerRoutineRecommendationResult actual = service.recommend(command());

        ArgumentCaptor<TrainerRoutineAiPort.TrainerWorkoutData> workouts = ArgumentCaptor.forClass(TrainerRoutineAiPort.TrainerWorkoutData.class);
        verify(trainerRoutineAiPort).recommend(eq(command()), workouts.capture());
        assertThat(actual.title()).isEqualTo("recommended");
        assertThat(workouts.getValue().recentWorkouts()).singleElement().satisfies(workout -> {
            assertThat(workout.exercise()).isEqualTo("Bench Press");
            assertThat(workout.sets()).singleElement().satisfies(set -> assertThat(set.reps()).isEqualTo(10));
        });
        assertThat(workouts.getValue().summary().periodDays()).isEqualTo(28);
        assertThat(workouts.getValue().summary().partSessionCounts()).containsEntry("CHEST", 1);
        assertThat(workouts.getValue().summary().partTotalVolumeKg()).containsEntry("CHEST", new BigDecimal("600"));
    }

    @Test
    void recommend_rejectsMemberWithoutActivePtRelationBeforeReadingWorkoutHistory() {
        when(snapshotService.load(command())).thenThrow(new TrainerRoutineRecommendationException(
                com.ssambbong.gymjjak.trainer.routinerecommendation.domain.exception.TrainerRoutineRecommendationErrorCode.MEMBER_ACCESS_DENIED));

        assertThatThrownBy(() -> service.recommend(command()))
                .isInstanceOf(TrainerRoutineRecommendationException.class);

        verifyNoInteractions(trainerRoutineAiPort);
    }

    private RecommendTrainerRoutineCommand command() {
        return new RecommendTrainerRoutineCommand(20L, 10L, TrainerRoutineGender.MALE, 28,
                new BigDecimal("175.5"), new BigDecimal("72.3"), TrainerRoutineGoal.MUSCLE_GAIN);
    }

    private TrainerRoutineAiPort.TrainerWorkoutData workoutData() {
        return new TrainerRoutineAiPort.TrainerWorkoutData(
                List.of(new TrainerRoutineAiPort.TrainerWorkoutSnapshot(LocalDate.now().toString(), "CHEST", "Bench Press",
                        List.of(new TrainerRoutineAiPort.TrainerWorkoutSetSnapshot(1, new BigDecimal("60"), 10)))),
                new TrainerRoutineAiPort.TrainerWorkoutSummary(28, 1, java.util.Map.of("CHEST", 1),
                        java.util.Map.of("CHEST", new BigDecimal("600"))));
    }

    private TrainerRoutineRecommendationResult result() {
        return new TrainerRoutineRecommendationResult("COMPLETE", "recommended", "summary", List.of(), List.of(), List.of(), List.of());
    }
}
