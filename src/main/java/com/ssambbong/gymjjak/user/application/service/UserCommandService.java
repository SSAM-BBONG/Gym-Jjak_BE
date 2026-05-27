package com.ssambbong.gymjjak.user.application.service;

import com.ssambbong.gymjjak.global.domain.common.exception.CommonErrorCode;
import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;
import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
import com.ssambbong.gymjjak.user.application.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.application.exception.UserException;
import com.ssambbong.gymjjak.user.application.port.in.UserCommandUseCase;
import com.ssambbong.gymjjak.user.application.port.out.PasswordEncodePort;
import com.ssambbong.gymjjak.user.application.port.out.UserRepositoryPort;
import com.ssambbong.gymjjak.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService implements UserCommandUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncodePort passwordEncodePort;
    private final Clock clock;

    @Override
    public void registerUser(RegisterUserCommand command) {

        log.debug("[UserRegister] request username={}, nickname={}, phone={}",
                command.username(),
                command.nickname(),
                maskPhone(command.phone())
        );

        validateDuplicateUsername(command.username());
        validateDuplicateNickname(command.nickname());
        validateDuplicatePhone(command.phone());

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

        log.info("[UserRegisterSuccess] username={}, nickname={}",
                command.username(),
                command.nickname()
        );
    }

    private void validateDuplicateUsername(String username) {
        if (userRepositoryPort.existsByUsername(username)) {
            log.warn("[DuplicateUserRegister] type=username, username={}", username);
            throw new UserException(UserErrorCode.DUPLICATE_USERNAME);
        }
    }

    private void validateDuplicateNickname(String nickname) {
        if (userRepositoryPort.existsByNickname(nickname)) {
            log.warn("[DuplicateUserRegister] type=nickname, nickname={}", nickname);
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validateDuplicatePhone(String phone) {
        if (userRepositoryPort.existsByPhone(phone)) {
            log.warn("[DuplicateUserRegister] type=phone, phone={}", maskPhone(phone));
            throw new UserException(UserErrorCode.DUPLICATE_PHONE);
        }
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 8) {
            return "****";
        }

        return phone.substring(0, 3) + "-****-" + phone.substring(phone.length() - 4);
    }
}
