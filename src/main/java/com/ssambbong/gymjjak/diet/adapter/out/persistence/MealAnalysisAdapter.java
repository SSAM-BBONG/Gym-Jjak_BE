package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.application.port.out.MealAnalysisPort;
import com.ssambbong.gymjjak.diet.application.query.MealPageQuery;
import com.ssambbong.gymjjak.diet.application.result.MealPageResult;
import com.ssambbong.gymjjak.diet.application.result.MealNutritionSummary;
import com.ssambbong.gymjjak.diet.domain.exception.MealAnalysisNotFoundException;
import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.math.BigDecimal;

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
                    .orElseThrow(() -> new MealAnalysisNotFoundException(meal.getId()));
            entity.update(meal);
        }
        return persistenceMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<MealAnalysis> findByIdAndUserId(Long mealId, Long userId) {
        return repository.findByIdAndUserId(mealId, userId).map(persistenceMapper::toDomain);
    }

    @Override
    public MealPageResult<MealAnalysis> findAllByUserId(MealPageQuery query) {
        PageRequest pageable = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(Sort.Order.desc("mealTime"), Sort.Order.desc("id"))
        );
        Page<MealAnalysisJpaEntity> entityPage;
        if (query.date() == null) {
            entityPage = repository.findAllByUserId(query.targetUserId(), pageable);
        } else {
            LocalDateTime startInclusive = query.date().atStartOfDay();
            LocalDateTime endExclusive = query.date().plusDays(1).atStartOfDay();
            entityPage = repository.findAllByUserIdAndMealTimeGreaterThanEqualAndMealTimeLessThan(
                    query.targetUserId(), startInclusive, endExclusive, pageable);
        }
        Page<MealAnalysis> page = entityPage.map(persistenceMapper::toDomain);
        return new MealPageResult<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.hasNext());
    }

    @Override
    public int deleteByIdAndUserId(Long mealId, Long userId) {
        return repository.deleteByIdAndUserId(mealId, userId);
    }

    @Override
    public MealNutritionSummary sumNutritionByUserIdAndMealTimeBetween(
            Long userId, LocalDateTime startInclusive, LocalDateTime endExclusive) {
        SpringDataMealAnalysisRepository.NutritionSumProjection sum =
                repository.sumNutritionByUserIdAndMealTimeBetween(userId, startInclusive, endExclusive);
        if (sum == null) {
            return MealNutritionSummary.empty();
        }
        return new MealNutritionSummary(
                sum.getKcal() == null ? 0L : sum.getKcal(),
                zeroIfNull(sum.getCarbohydrate()),
                zeroIfNull(sum.getProtein()),
                zeroIfNull(sum.getFat())
        );
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
