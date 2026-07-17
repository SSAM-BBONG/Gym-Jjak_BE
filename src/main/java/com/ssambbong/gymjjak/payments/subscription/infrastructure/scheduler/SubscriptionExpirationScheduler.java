package com.ssambbong.gymjjak.payments.subscription.infrastructure.scheduler;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

    private final SpringDataSubscriptionRepository subscriptionRepository;
    private final SpringDataUserRepository userRepository;
    private final Clock clock;

    // 매시간 만료 시각이 지난 활성 구독을 정리한다.
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    @Transactional
    public void expireSubscriptions() {
        LocalDateTime now = LocalDateTime.now(clock);
        var expiredSubscriptions = subscriptionRepository
                .findAllByStatusAndExpiredAtLessThanEqualOrderByUserIdAsc(SubscriptionStatus.ACTIVE, now);

        for (var candidate : expiredSubscriptions) {
            // 결제·환불과 같은 사용자 단위 상태 변경과 충돌하지 않도록 사용자 행부터 잠근다.
            var user = userRepository.findByIdForUpdate(candidate.getUserId()).orElse(null);
            var subscription = subscriptionRepository.findByIdForUpdate(candidate.getId()).orElse(null);
            if (user == null || subscription == null
                    || subscription.getStatus() != SubscriptionStatus.ACTIVE
                    || subscription.getExpiredAt().isAfter(now)) {
                continue;
            }

            subscription.expire();
            boolean hasAnotherActiveSubscription = subscriptionRepository
                    .existsByUserIdAndStatusAndExpiredAtAfter(
                            subscription.getUserId(), SubscriptionStatus.ACTIVE, now);
            if (!hasAnotherActiveSubscription) {
                user.markAsUnpaid();
            }
        }

        log.info("event=subscription_expiration_completed count={}", expiredSubscriptions.size());
    }
}
