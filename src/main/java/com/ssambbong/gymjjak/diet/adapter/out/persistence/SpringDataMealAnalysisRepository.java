package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataMealAnalysisRepository extends JpaRepository<MealAnalysisJpaEntity, Long> {
    Optional<MealAnalysisJpaEntity> findByIdAndUserId(Long mealId, Long userId);
    Page<MealAnalysisJpaEntity> findAllByUserId(Long userId, Pageable pageable);
}
