package com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.out;

import com.ssambbong.gymjjak.trainer.routinerecommendation.application.command.RecommendTrainerRoutineCommand;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.result.TrainerRoutineRecommendationResult;

import java.util.List;

public interface TrainerRoutineAiPort {
    TrainerRoutineRecommendationResult recommend(
            RecommendTrainerRoutineCommand command,
            TrainerWorkoutData workoutData
    );

    record TrainerWorkoutData(List<TrainerWorkoutSnapshot> recentWorkouts, TrainerWorkoutSummary summary) {
    }

    record TrainerWorkoutSummary(
            int periodDays,
            int workoutDays,
            java.util.Map<String, Integer> partSessionCounts,
            java.util.Map<String, java.math.BigDecimal> partTotalVolumeKg
    ) {
    }

    record TrainerWorkoutSnapshot(
            String diaryDate,
            String part,
            String exercise,
            List<TrainerWorkoutSetSnapshot> sets
    ) {
    }

    record TrainerWorkoutSetSnapshot(int setNumber, java.math.BigDecimal weight, int reps) {
    }
}
