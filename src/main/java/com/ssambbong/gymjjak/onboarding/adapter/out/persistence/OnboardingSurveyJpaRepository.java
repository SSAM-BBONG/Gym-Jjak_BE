package com.ssambbong.gymjjak.onboarding.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingSurveyJpaRepository extends JpaRepository<OnboardingSurveyJpaEntity, Long> {

    boolean existsByUserId(Long userId);
}
