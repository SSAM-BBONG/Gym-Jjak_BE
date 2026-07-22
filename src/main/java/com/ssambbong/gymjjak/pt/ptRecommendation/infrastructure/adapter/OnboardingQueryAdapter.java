package com.ssambbong.gymjjak.pt.ptRecommendation.infrastructure.adapter;

import com.ssambbong.gymjjak.onboarding.application.port.in.OnboardingUsecase;
import com.ssambbong.gymjjak.onboarding.application.result.MyOnboardingResult;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.port.OnboardingQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// OnboardingQueryPort 구현체. 실제 조회(캐싱 포함)는 onboarding 도메인의 OnboardingUsecase에
// 그대로 위임하고, PT추천에 필요한 필드만 골라 MyOnboardingInfo로 좁혀서 반환한다.
@Component
@RequiredArgsConstructor
public class OnboardingQueryAdapter implements OnboardingQueryPort {

    private final OnboardingUsecase onboardingUsecase;

    @Override
    public MyOnboardingInfo findMyOnboarding(Long userId) {
        MyOnboardingResult onboarding = onboardingUsecase.getMyOnboarding(userId);
        MyOnboardingResult.RegionResult region = onboarding.preferredRegion();
        return new MyOnboardingInfo(
                onboarding.exerciseGoal(),
                onboarding.exercisePeriod(),
                onboarding.exerciseFrequency(),
                region.latitude(),
                region.longitude());
    }
}
