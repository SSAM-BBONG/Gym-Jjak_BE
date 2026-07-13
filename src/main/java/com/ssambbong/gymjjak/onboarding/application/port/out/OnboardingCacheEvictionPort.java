package com.ssambbong.gymjjak.onboarding.application.port.out;

public interface OnboardingCacheEvictionPort {

    void evictMyOnboarding(Long userId);
}
