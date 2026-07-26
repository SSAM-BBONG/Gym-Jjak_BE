package com.ssambbong.gymjjak.trainer.routinerecommendation.application.port.out;

import com.ssambbong.gymjjak.trainer.routinerecommendation.application.command.RecommendTrainerRoutineCommand;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.result.TrainerRoutineRecommendationResult;

import java.util.List;

public interface TrainerRoutineAiPort {
    TrainerRoutineRecommendationResult recommend(
            RecommendTrainerRoutineCommand command,
            List<TrainerWorkoutSnapshot> workouts
    );

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
