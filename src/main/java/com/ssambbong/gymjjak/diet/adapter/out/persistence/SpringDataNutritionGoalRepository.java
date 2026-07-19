package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface SpringDataNutritionGoalRepository extends JpaRepository<NutritionGoalJpaEntity, Long> {
    Optional<NutritionGoalJpaEntity> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM NutritionGoalJpaEntity n WHERE n.userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);
}
