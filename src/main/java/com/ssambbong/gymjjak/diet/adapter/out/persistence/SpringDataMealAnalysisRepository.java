package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataMealAnalysisRepository extends JpaRepository<MealAnalysisJpaEntity, Long> {
    Optional<MealAnalysisJpaEntity> findByIdAndUserId(Long mealId, Long userId);
    Page<MealAnalysisJpaEntity> findAllByUserId(Long userId, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM MealAnalysisJpaEntity m WHERE m.id = :mealId AND m.userId = :userId")
    int deleteByIdAndUserId(@Param("mealId") Long mealId, @Param("userId") Long userId);
}
