package com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
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

    // 만료 처리에 필요한 ID만 조회하여 관리 엔티티 적재를 최소화한다.
    @Query("""
            select s.id as subscriptionId, s.userId as userId
            from SubscriptionJpaEntity s
            where s.status = :status
              and s.expiredAt <= :now
            """)
    Slice<ExpirationCandidate> findExpirationCandidates(
            @Param("status") SubscriptionStatus status,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    // 조회 이후 상태가 바뀐 경우까지 방어하도록 상태와 만료 시각을 다시 조건으로 확인한다.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update SubscriptionJpaEntity s
            set s.status = :expiredStatus
            where s.id in :subscriptionIds
              and s.status = :activeStatus
              and s.expiredAt <= :now
            """)
    int expireAll(
            @Param("subscriptionIds") java.util.Collection<Long> subscriptionIds,
            @Param("activeStatus") SubscriptionStatus activeStatus,
            @Param("expiredStatus") SubscriptionStatus expiredStatus,
            @Param("now") LocalDateTime now
    );

    interface ExpirationCandidate {
        Long getSubscriptionId();
        Long getUserId();
    }
}
