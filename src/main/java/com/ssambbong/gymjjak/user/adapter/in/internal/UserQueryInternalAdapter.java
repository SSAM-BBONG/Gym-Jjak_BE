package com.ssambbong.gymjjak.user.adapter.in.internal;

import com.ssambbong.gymjjak.report.application.port.user.UserProfileView;
import com.ssambbong.gymjjak.report.application.port.user.UserQueryPort;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserQueryInternalAdapter implements UserQueryPort{

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public Optional<UserProfileView> findUserProfile(Long userId) {
        return springDataUserRepository.findById(userId)
                .map(user -> new UserProfileView(
                        user.getId(),
                        user.getUsername()
                ));
    }
}
