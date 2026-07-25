package com.ssambbong.gymjjak.trainer.routinerecommendation.application.command;

import com.ssambbong.gymjjak.trainer.routinerecommendation.application.model.TrainerRoutineGoal;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.model.TrainerRoutineGender;

import java.math.BigDecimal;

public record RecommendTrainerRoutineCommand(
        Long trainerUserId,
        Long memberUserId,
        TrainerRoutineGender gender,
        int age,
        BigDecimal heightCm,
        BigDecimal weightKg,
        TrainerRoutineGoal goal
) {
}
