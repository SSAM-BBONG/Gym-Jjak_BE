package com.ssambbong.gymjjak.onboarding.application.port.in;

import com.ssambbong.gymjjak.onboarding.application.command.RegisterOnboardingCommand;

public interface OnboardingUsecase {

    void complete(RegisterOnboardingCommand command);
}
