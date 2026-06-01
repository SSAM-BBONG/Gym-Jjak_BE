package com.ssambbong.gymjjak.onboarding.application.port.out;

import com.ssambbong.gymjjak.onboarding.adapter.out.persistence.OnboardingSurveyJpaEntity;
import com.ssambbong.gymjjak.onboarding.domain.model.OnboardingSurvey;
import com.ssambbong.gymjjak.onboarding.domain.model.Region;

import java.util.Optional;

public interface OnboardingPort {

    boolean existsByUserId(Long userId);

    Long saveRegion(Region region);

    void saveOnboardingSurvey(OnboardingSurvey onboardingSurvey);

    void completeUserOnboarding(Long userId);

    Optional<MyOnboardingView> findMyOnboardingByUserId(Long userId);

}
