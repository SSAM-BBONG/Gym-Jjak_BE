package com.ssambbong.gymjjak.global.infrastructure.security.oauth;

import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
    }

    public static OAuth2UserInfo of(
            String registrationId,
            Map<String, Object> attributes
    ) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "naver" -> new NaverOAuth2UserInfo(attributes);
            default -> throw new UserException(UserErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
        };
    }
}
