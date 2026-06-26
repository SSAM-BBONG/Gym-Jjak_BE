package com.ssambbong.gymjjak.user.adapter.in.internal;

import com.ssambbong.gymjjak.organization.organizationApplication.application.port.UserLoginIdValidationPort;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.DuplicateRequestedLoginIdException;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.InvalidLoginIdFormatException;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserLoginIdValidationAdapter implements UserLoginIdValidationPort {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public void validate(String loginId) {
        if (!EMAIL_PATTERN.matcher(loginId).matches()) {
            throw new InvalidLoginIdFormatException();
        }
        if (springDataUserRepository.existsByUsername(loginId)) {
            throw new DuplicateRequestedLoginIdException();
        }
    }
}
