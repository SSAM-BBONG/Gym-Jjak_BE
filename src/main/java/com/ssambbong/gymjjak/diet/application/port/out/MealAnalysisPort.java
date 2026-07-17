package com.ssambbong.gymjjak.diet.application.port.out;

import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MealAnalysisPort {
    MealAnalysis save(MealAnalysis mealAnalysis);
    Optional<MealAnalysis> findByIdAndUserId(Long mealId, Long userId);
    Page<MealAnalysis> findAllByUserId(Long userId, Pageable pageable);
    void deleteById(Long mealId);
}
