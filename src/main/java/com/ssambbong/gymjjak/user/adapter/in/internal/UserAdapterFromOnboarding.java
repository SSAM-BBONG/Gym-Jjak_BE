package com.ssambbong.gymjjak.user.adapter.in.internal;

import com.ssambbong.gymjjak.onboarding.application.port.out.UserPortFromOnboarding;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.UserJpaEntity;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAdapterFromOnboarding implements UserPortFromOnboarding {

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public boolean existsById(Long userId) {
        return springDataUserRepository.existsById(userId);
    }

    @Override
    public void completeOnboarding(Long userId) {
        UserJpaEntity user = springDataUserRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        user.completeOnboarding();
    }

}
