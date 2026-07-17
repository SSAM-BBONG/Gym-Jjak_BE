package com.ssambbong.gymjjak.payments.subscription.infrastructure.scheduler;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionExpirationBatchService {

    private final SpringDataSubscriptionRepository subscriptionRepository;
    private final SpringDataUserRepository userRepository;

    @Transactional
    public SubscriptionExpirationBatchResult expireNextBatch(LocalDateTime now, int batchSize) {
        // 처리된 구독은 조회 조건에서 제외되므로 offset을 증가시키지 않고 항상 첫 Slice를 조회한다.
        PageRequest pageable = PageRequest.of(
                0,
                batchSize,
                Sort.by(Sort.Order.asc("userId"), Sort.Order.asc("id"))
        );
        var candidates = subscriptionRepository.findByStatusAndExpiredAtLessThanEqual(
                SubscriptionStatus.ACTIVE, now, pageable);

        int expiredCount = 0;
        int unpaidUserCount = 0;

        for (var candidate : candidates.getContent()) {
            // 모든 구독 상태 변경은 사용자 행을 먼저 잠가 동일 사용자의 처리를 직렬화한다.
            var user = userRepository.findByIdForUpdate(candidate.getUserId()).orElse(null);
            var subscription = subscriptionRepository.findByIdForUpdate(candidate.getId()).orElse(null);
            if (user == null || subscription == null
                    || subscription.getStatus() != SubscriptionStatus.ACTIVE
                    || subscription.getExpiredAt().isAfter(now)) {
                continue;
            }

            subscription.expire();
            expiredCount++;

            boolean hasAnotherActiveSubscription = subscriptionRepository
                    .existsByUserIdAndStatusAndExpiredAtAfter(
                            subscription.getUserId(), SubscriptionStatus.ACTIVE, now);
            if (!hasAnotherActiveSubscription) {
                user.markAsUnpaid();
                unpaidUserCount++;
            }
        }

        return new SubscriptionExpirationBatchResult(
                candidates.getNumberOfElements(), expiredCount, unpaidUserCount);
    }
}
