package com.ssambbong.gymjjak.global.infrastructure.security.oauth;

import com.ssambbong.gymjjak.user.application.result.SocialLoginResult;
import com.ssambbong.gymjjak.user.application.service.OAuth2LoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2LoginService oAuth2LoginService;

    @Value("${app.oauth2.success-redirect-url}")
    private String successRedirectUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        SocialLoginResult result = oAuth2LoginService.login(
                registrationId,
                oauthToken.getPrincipal()
        );

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", result.accessToken())
                .httpOnly(true)
                .secure(false) // 로컬 개발 환경
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());

        String redirectUrl = UriComponentsBuilder
                .fromUriString(successRedirectUrl)
                .queryParam("role", result.role())
                .queryParam("onboardingCompleted", result.onboardingCompleted())
                .queryParam("socialSignupCompleted", result.socialSignupCompleted())
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
