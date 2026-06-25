package com.ssambbong.gymjjak.global.infrastructure.security.oauth;

import com.ssambbong.gymjjak.user.application.result.SocialLoginResult;
import com.ssambbong.gymjjak.user.application.service.OAuth2LoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Slf4j
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

        log.info("oauth2_success_handler_started");
        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        log.info("oauth2_registration_id={}", registrationId);
        log.info("oauth2_principal_attributes={}", oauthToken.getPrincipal().getAttributes());

        SocialLoginResult result = oAuth2LoginService.login(
                registrationId,
                oauthToken.getPrincipal()
        );

        log.info("oauth2_login_service_success, role={}, onboardingCompleted={}, socialSignupCompleted={}",
                result.role(),
                result.onboardingCompleted(),
                result.socialSignupCompleted()
        );

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", result.refreshToken())
                .httpOnly(true)
                .secure(false) // 로컬 개발 환경
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(15))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        String redirectUrl = UriComponentsBuilder
                .fromUriString(successRedirectUrl)
                .queryParam("accessToken", result.accessToken())
                .queryParam("onboardingCompleted", result.onboardingCompleted())
                .queryParam("socialSignupCompleted", result.socialSignupCompleted())
                .build()
                .toUriString();

        log.info("oauth2_redirect_url={}", redirectUrl);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
