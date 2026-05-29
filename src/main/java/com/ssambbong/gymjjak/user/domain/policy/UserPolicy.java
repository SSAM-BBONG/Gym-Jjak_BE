package com.ssambbong.gymjjak.user.domain.policy;

import com.ssambbong.gymjjak.user.application.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.application.exception.UserException;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.domain.model.User;
import com.ssambbong.gymjjak.user.domain.model.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPolicy {

    private final UserPort userPort;

    public void validateDuplicateUsername(String username) {
        if (userPort.existsByUsername(username)) {
            log.warn("[DuplicateUserRegister] type=username, username={}", username);
            throw new UserException(UserErrorCode.DUPLICATE_USERNAME);
        }
    }

    public void validateDuplicateNickname(String nickname) {
        if (userPort.existsByNickname(nickname)) {
            log.warn("[DuplicateUserRegister] type=nickname, nickname={}", nickname);
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateDuplicatePhone(String phone) {
        if (userPort.existsByPhone(phone)) {
            log.warn("[DuplicateUserRegister] type=phone, phone={}", maskPhone(phone));
            throw new UserException(UserErrorCode.DUPLICATE_PHONE);
        }
    }

    public void validatePasswordPolicy(String password) {
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$")) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }
    }

    public void validateLoginAllowed(User user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("[UserLoginFailed] reason=restricted_status, userId={}, username={}, status={}",
                    user.getId(),
                    user.getUsername(),
                    user.getStatus()
            );
            throw new UserException(UserErrorCode.LOGIN_RESTRICTED);
        }
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 8) {
            return "****";
        }
        return phone.substring(0, 3) + "-****-" + phone.substring(phone.length() - 4);
    }
}
