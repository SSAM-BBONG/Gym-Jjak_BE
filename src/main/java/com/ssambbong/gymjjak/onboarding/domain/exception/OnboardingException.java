package com.ssambbong.gymjjak.onboarding.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

public class OnboardingException extends ApplicationException {
    public OnboardingException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}
