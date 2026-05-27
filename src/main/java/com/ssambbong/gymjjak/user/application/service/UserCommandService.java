package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
import com.ssambbong.gymjjak.user.application.port.in.UserCommandUseCase;
import com.ssambbong.gymjjak.user.application.port.out.PasswordEncodePort;
import com.ssambbong.gymjjak.user.application.port.out.UserRepositoryPort;
import com.ssambbong.gymjjak.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService implements UserCommandUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncodePort passwordEncodePort;
    private final Clock clock;

    @Override
    public void registerUser(RegisterUserCommand command) {
        validateDuplicateUsername(command.username());
        validateDuplicateNickname(command.nickname());

        String encodedPassword = passwordEncodePort.encode(command.password());

        Instant now = Instant.now(clock);

        User user = User.register(
                command.username(),
                encodedPassword,
                command.name(),
                command.nickname(),
                command.phone(),
                now
        );

        userRepositoryPort.save(user);
    }

    private void validateDuplicateUsername(String username) {
        if (userRepositoryPort.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    private void validateDuplicateNickname(String nickname) {
        if (userRepositoryPort.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
    }
}
