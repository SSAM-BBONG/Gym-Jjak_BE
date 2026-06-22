package com.ssambbong.gymjjak.global.infrastructure.security.oauth;

import com.ssambbong.gymjjak.user.domain.model.SocialProvider;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> response;

    @SuppressWarnings("unchecked")
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.NAVER;
    }

    @Override
    public String getSocialId() {
        return String.valueOf(response.get("id"));
    }

    @Override
    public String getUsername() {
        return String.valueOf(response.get("email"));
    }

    @Override
    public String getName() {
        return String.valueOf(response.get("name"));
    }
}
