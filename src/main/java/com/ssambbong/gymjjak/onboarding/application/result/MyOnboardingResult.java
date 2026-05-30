package com.ssambbong.gymjjak.onboarding.application.result;

import java.math.BigDecimal;

public record MyOnboardingResult(
        Long onboardingId,
        String exerciseGoal,
        String exercisePeriod,
        String exerciseFrequency,
        String preferredExercise,
        RegionResult preferredRegion,
        BigDecimal height,
        BigDecimal weight
) {
    public record RegionResult(
            Long regionId,
            String sido,
            String sigungu,
            String eupmyeondong,
            String fullName,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
    }
}
