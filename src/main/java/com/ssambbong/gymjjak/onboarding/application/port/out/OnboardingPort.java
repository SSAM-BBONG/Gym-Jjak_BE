package com.ssambbong.gymjjak.onboarding.application.port.out;

import com.ssambbong.gymjjak.onboarding.domain.model.OnboardingSurvey;
import com.ssambbong.gymjjak.onboarding.domain.model.Region;

public interface OnboardingPort {

    boolean existsByUserId(Long userId);

    Long saveRegion(Region region);

    void saveOnboardingSurvey(OnboardingSurvey onboardingSurvey);

    void completeUserOnboarding(Long userId);
}
