package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.global.infrastructure.security.oauth.OAuth2UserInfo;
import com.ssambbong.gymjjak.global.infrastructure.security.oauth.OAuth2UserInfoFactory;
import com.ssambbong.gymjjak.user.application.port.out.TokenPort;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.application.result.SocialLoginResult;
import com.ssambbong.gymjjak.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2LoginService {

    private final UserPort userPort;
    private final TokenPort tokenPort;

    public SocialLoginResult login(
            String registrationId,
            OAuth2User oauth2User
    ) {
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.of(
                registrationId,
                oauth2User.getAttributes()
        );

        User user = userPort.findBySocialProviderAndSocialId(
                        userInfo.getProvider(),
                        userInfo.getSocialId()
                )
                .orElseGet(() -> registerSocialUser(userInfo));

        user.validateLoginAllowed();

        LocalDateTime now = LocalDateTime.now();

        user.markLoggedIn(now);

        userPort.updateLastLoginAt(
                user.getId(),
                user.getLastLoginAt()
        );

        String accessToken = tokenPort.createAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        String refreshToken = tokenPort.createRefreshToken(
                user.getId(),
                user.getUsername()
        );

        tokenPort.saveOrUpdateRefreshToken(
                user.getId(),
                refreshToken
        );

        return new SocialLoginResult(
                accessToken,
                refreshToken,
                user.getRole().name(),
                user.isOnboardingCompleted(),
                user.isSocialSignupCompleted()
        );
    }

    private User registerSocialUser(OAuth2UserInfo userInfo) {
        User socialUser = User.registerSocial(
                userInfo.getUsername(),
                userInfo.getName(),
                userInfo.getProvider(),
                userInfo.getSocialId(),
                LocalDateTime.now()
        );

        return userPort.save(socialUser);
    }
}
