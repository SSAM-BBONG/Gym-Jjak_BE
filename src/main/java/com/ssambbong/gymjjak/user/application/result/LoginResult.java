package com.ssambbong.gymjjak.user.application.result;

public record LoginResult(
        String accessToken,
        String refreshToken,
        boolean onboardingCompleted
) {
}
