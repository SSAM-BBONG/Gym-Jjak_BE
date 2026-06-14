package com.ssambbong.gymjjak.onboarding.application.port.in;

import com.ssambbong.gymjjak.onboarding.application.command.RegisterOnboardingCommand;
import com.ssambbong.gymjjak.onboarding.application.command.UpdateOnboardingCommand;
import com.ssambbong.gymjjak.onboarding.application.result.MyOnboardingResult;

public interface OnboardingUsecase {

    void register(RegisterOnboardingCommand command);

    MyOnboardingResult getMyOnboarding(Long userId);

    void updateOnboarding(UpdateOnboardingCommand command);
}
