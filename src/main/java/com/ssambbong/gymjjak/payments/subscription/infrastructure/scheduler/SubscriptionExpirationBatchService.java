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
import java.util.List;

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
        var candidates = subscriptionRepository.findExpirationCandidates(
                SubscriptionStatus.ACTIVE, now, pageable);

        if (candidates.isEmpty()) {
            return new SubscriptionExpirationBatchResult(0, 0, 0);
        }

        List<Long> subscriptionIds = candidates.getContent().stream()
                .map(SpringDataSubscriptionRepository.ExpirationCandidate::getSubscriptionId)
                .toList();
        List<Long> userIds = candidates.getContent().stream()
                .map(SpringDataSubscriptionRepository.ExpirationCandidate::getUserId)
                .distinct()
                .sorted()
                .toList();

        // 건별 잠금 대신 대상 사용자를 한 번에 잠가 결제 웹훅과의 동시성을 제어한다.
        userRepository.findAllByIdForUpdate(userIds);

        // 구독 만료와 사용자 권한 회수를 각각 벌크 쿼리로 처리해 N+1 조회를 제거한다.
        int expiredCount = subscriptionRepository.expireAll(
                subscriptionIds, SubscriptionStatus.ACTIVE, SubscriptionStatus.EXPIRED, now);
        int unpaidUserCount = userRepository.markUnpaidWithoutActiveSubscription(userIds, now);

        return new SubscriptionExpirationBatchResult(
                candidates.getNumberOfElements(), expiredCount, unpaidUserCount);
    }
}
