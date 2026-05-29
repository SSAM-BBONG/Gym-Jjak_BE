package com.ssambbong.gymjjak.onboarding.application.command;

import java.math.BigDecimal;

public record RegisterOnboardingCommand(
        Long userId,
        String exerciseGoal,
        String exercisePeriod,
        String exerciseFrequency,
        String preferredExercise,
        BigDecimal height,
        BigDecimal weight,
        RegionCommand region
) {
}
