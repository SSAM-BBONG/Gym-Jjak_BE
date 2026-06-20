package com.ssambbong.gymjjak.user.application.result;

public record SocialLoginResult(
        String accessToken,
        String refreshToken,
        String role,
        boolean onboardingCompleted,
        boolean socialSignupCompleted
) {
}
