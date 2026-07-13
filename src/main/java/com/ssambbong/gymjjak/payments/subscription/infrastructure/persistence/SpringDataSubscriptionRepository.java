package com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSubscriptionRepository extends JpaRepository<SubscriptionJpaEntity, Long> {}
