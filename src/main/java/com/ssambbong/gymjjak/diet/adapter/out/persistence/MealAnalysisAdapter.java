package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.application.port.out.MealAnalysisPort;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MealAnalysisAdapter implements MealAnalysisPort {

    private final SpringDataMealAnalysisRepository repository;
    private final MealAnalysisPersistenceMapper persistenceMapper;

    @Override
    public MealAnalysis save(MealAnalysis meal) {
        MealAnalysisJpaEntity entity;
        if (meal.getId() == null) {
            entity = persistenceMapper.toEntity(meal);
        } else {
            entity = repository.findById(meal.getId())
                    .orElseGet(() -> persistenceMapper.toEntity(meal));
            entity.update(meal);
        }
        return persistenceMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<MealAnalysis> findByIdAndUserId(Long mealId, Long userId) {
        return repository.findByIdAndUserId(mealId, userId).map(persistenceMapper::toDomain);
    }

    @Override
    public Page<MealAnalysis> findAllByUserId(Long userId, Pageable pageable) {
        return repository.findAllByUserId(userId, pageable).map(persistenceMapper::toDomain);
    }

    @Override
    public void deleteById(Long mealId) {
        repository.deleteById(mealId);
    }
}
