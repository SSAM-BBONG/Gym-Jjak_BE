package com.ssambbong.gymjjak.onboarding.application.port.out;

public interface UserPortFromOnboarding {
    boolean existsById(Long userId);

    void completeOnboarding(Long userId);

}
