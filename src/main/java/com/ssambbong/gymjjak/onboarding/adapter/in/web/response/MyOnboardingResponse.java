package com.ssambbong.gymjjak.onboarding.adapter.in.web.response;

import com.ssambbong.gymjjak.onboarding.application.result.MyOnboardingResult;

import java.math.BigDecimal;

public record MyOnboardingResponse(
        Long onboardingId,
        String exerciseGoal,
        String exercisePeriod,
        String exerciseFrequency,
        String preferredExercise,
        RegionResponse preferredRegion,
        BigDecimal height,
        BigDecimal weight
) {
    public static MyOnboardingResponse from(MyOnboardingResult result) {
        return new MyOnboardingResponse(
                result.onboardingId(),
                result.exerciseGoal(),
                result.exercisePeriod(),
                result.exerciseFrequency(),
                result.preferredExercise(),
                new RegionResponse(
                        result.preferredRegion().regionId(),
                        result.preferredRegion().sido(),
                        result.preferredRegion().sigungu(),
                        result.preferredRegion().eupmyeondong(),
                        result.preferredRegion().fullName(),
                        result.preferredRegion().latitude(),
                        result.preferredRegion().longitude()
                ),
                result.height(),
                result.weight()
        );
    }

    public record RegionResponse(
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
