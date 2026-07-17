package com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface SpringDataSubscriptionRepository extends JpaRepository<SubscriptionJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SubscriptionJpaEntity s where s.id = :subscriptionId")
    Optional<SubscriptionJpaEntity> findByIdForUpdate(@Param("subscriptionId") Long subscriptionId);

    // 활성 구독 조회
    Optional<SubscriptionJpaEntity> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    // 활성 구독 존재 여부
    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);

    Optional<SubscriptionJpaEntity> findByUserIdAndStatusAndExpiredAtAfter(
            Long userId, SubscriptionStatus status, LocalDateTime now);

    boolean existsByUserIdAndStatusAndExpiredAtAfter(
            Long userId, SubscriptionStatus status, LocalDateTime now);

    List<SubscriptionJpaEntity> findAllByStatusAndExpiredAtLessThanEqualOrderByUserIdAsc(
            SubscriptionStatus status, LocalDateTime now);
}
