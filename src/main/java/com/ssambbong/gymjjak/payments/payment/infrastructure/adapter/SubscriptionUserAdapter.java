package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionUserPort;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.UserJpaEntity;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionUserAdapter implements SubscriptionUserPort {

    private final SpringDataUserRepository userRepository;

    @Override
    public void lockById(Long userId) {
        findLockedUser(userId);
    }

    @Override
    public void markAsPaid(Long userId) {
        findLockedUser(userId).markAsPaid();
    }

    @Override
    public void markAsUnpaid(Long userId) {
        findLockedUser(userId).markAsUnpaid();
    }

    private UserJpaEntity findLockedUser(Long userId) {
        return userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}
