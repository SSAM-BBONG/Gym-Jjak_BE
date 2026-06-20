package com.ssambbong.gymjjak.global.infrastructure.security.oauth;

import com.ssambbong.gymjjak.user.domain.model.SocialProvider;

public interface OAuth2UserInfo {
    SocialProvider getProvider();

    String getSocialId();

    String getUsername();

    String getName();
}
