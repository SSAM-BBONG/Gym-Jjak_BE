package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.application.port.out.NutritionGoalPort;
import com.ssambbong.gymjjak.diet.domain.exception.DuplicateNutritionGoalException;
import com.ssambbong.gymjjak.diet.domain.exception.NutritionGoalNotFoundException;
import com.ssambbong.gymjjak.diet.domain.model.NutritionGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NutritionGoalAdapter implements NutritionGoalPort {
    private final SpringDataNutritionGoalRepository repository;

    @Override
    public NutritionGoal save(NutritionGoal goal) {
        NutritionGoalJpaEntity entity;
        if (goal.getId() == null) {
            entity = NutritionGoalJpaEntity.from(goal);
        } else {
            entity = repository.findById(goal.getId()).orElseThrow(NutritionGoalNotFoundException::new);
            entity.update(goal);
        }
        try {
            return repository.saveAndFlush(entity).toDomain();
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateNutritionGoalException(exception);
        }
    }

    @Override
    public Optional<NutritionGoal> findByUserId(Long userId) {
        return repository.findByUserId(userId).map(NutritionGoalJpaEntity::toDomain);
    }
    @Override public boolean existsByUserId(Long userId) { return repository.existsByUserId(userId); }
    @Override public int deleteByUserId(Long userId) { return repository.deleteByUserId(userId); }
}
