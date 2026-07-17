package com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataSubscriptionRepository extends JpaRepository<SubscriptionJpaEntity, Long> {

    // 활성 구독 조회
    Optional<SubscriptionJpaEntity> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    // 활성 구독 존재 여부
    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);
}
