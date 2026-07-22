package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SpringDataMealAnalysisRepository extends JpaRepository<MealAnalysisJpaEntity, Long> {
    Optional<MealAnalysisJpaEntity> findByIdAndUserId(Long mealId, Long userId);
    Page<MealAnalysisJpaEntity> findAllByUserId(Long userId, Pageable pageable);
    Page<MealAnalysisJpaEntity> findAllByUserIdAndMealTimeGreaterThanEqualAndMealTimeLessThan(
            Long userId, LocalDateTime startInclusive, LocalDateTime endExclusive, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM MealAnalysisJpaEntity m WHERE m.id = :mealId AND m.userId = :userId")
    int deleteByIdAndUserId(@Param("mealId") Long mealId, @Param("userId") Long userId);

    // 식사 시각을 기준으로 해당 날짜에 이미 섭취한 영양성분을 합산한다.
    @Query("""
            SELECT COALESCE(SUM(m.kcal), 0) AS kcal,
                   COALESCE(SUM(m.carbohydrate), 0) AS carbohydrate,
                   COALESCE(SUM(m.protein), 0) AS protein,
                   COALESCE(SUM(m.fat), 0) AS fat
            FROM MealAnalysisJpaEntity m
            WHERE m.userId = :userId
              AND m.mealTime >= :startInclusive
              AND m.mealTime < :endExclusive
            """)
    NutritionSumProjection sumNutritionByUserIdAndMealTimeBetween(
            @Param("userId") Long userId,
            @Param("startInclusive") LocalDateTime startInclusive,
            @Param("endExclusive") LocalDateTime endExclusive);

    interface NutritionSumProjection {
        Long getKcal();
        BigDecimal getCarbohydrate();
        BigDecimal getProtein();
        BigDecimal getFat();
    }
}
