package com.ssambbong.gymjjak.user.adapter.in.internal;

import com.ssambbong.gymjjak.organization.organizationApplication.application.port.UserCreationPort;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.UserJpaEntity;
import com.ssambbong.gymjjak.user.application.event.OrganizationAccountCreatedMailEvent;
import com.ssambbong.gymjjak.user.application.port.out.UserPort;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import com.ssambbong.gymjjak.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserMailAdapterFromOrganization implements UserCreationPort {

    private final SpringDataUserRepository springDataUserRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserPort userPort;

    @Override
    public Long createOrganizationAccount(
            String loginId,
            String representativeName,
            String businessName,
            String representativePhone
    ) {

        String temporaryPassword = generateTemporaryPassword();
        String encodedPassword = userPort.encode(temporaryPassword);

        User user = User.createOrganizationAccount(
                loginId,
                encodedPassword,
                representativeName,
                businessName,
                representativePhone,
                LocalDateTime.now()
        );

        validateDuplicate(loginId, businessName, representativePhone);

        UserJpaEntity savedUser = springDataUserRepository.save(UserJpaEntity.from(user));

        eventPublisher.publishEvent(
                new OrganizationAccountCreatedMailEvent(
                        loginId,
                        temporaryPassword
                )
        );

        return savedUser.getId();
    }

    private void validateDuplicate(
            String loginId,
            String businessName,
            String representativePhone
    ) {
        if (springDataUserRepository.existsByUsername(loginId)) {
            throw new UserException(UserErrorCode.DUPLICATE_USERNAME);
        }

        if (springDataUserRepository.existsByNickname(businessName)) {
            throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }

        if (springDataUserRepository.existsByPhone(representativePhone)) {
            throw new UserException(UserErrorCode.DUPLICATE_PHONE);
        }
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}
