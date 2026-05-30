package com.ssambbong.gymjjak.onboarding.application.port.out;

import java.math.BigDecimal;

public record MyOnboardingView(
        Long onboardingId,
        String exerciseGoal,
        String exercisePeriod,
        String exerciseFrequency,
        String preferredExercise,
        Long regionId,
        String sido,
        String sigungu,
        String eupmyeondong,
        String fullName,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal height,
        BigDecimal weight
) {
}
