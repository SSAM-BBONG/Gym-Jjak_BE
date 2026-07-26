package com.ssambbong.gymjjak.trainer.routinerecommendation.presentation.api.request;

import com.ssambbong.gymjjak.trainer.routinerecommendation.application.model.TrainerRoutineGender;
import com.ssambbong.gymjjak.trainer.routinerecommendation.application.model.TrainerRoutineGoal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RecommendTrainerRoutineRequest(
        @NotNull TrainerRoutineGender gender,
        @Min(14) @Max(100) int age,
        @NotNull @DecimalMin("0.1") @DecimalMax("300") BigDecimal heightCm,
        @NotNull @DecimalMin("0.1") @DecimalMax("500") BigDecimal weightKg,
        @NotNull TrainerRoutineGoal goal
) {
}
