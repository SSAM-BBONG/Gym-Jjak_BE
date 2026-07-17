package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.domain.model.MealAnalysis;
import org.springframework.stereotype.Component;

@Component
public class MealAnalysisPersistenceMapper {

    public MealAnalysisJpaEntity toEntity(MealAnalysis meal) {
        return MealAnalysisJpaEntity.from(meal);
    }

    public MealAnalysis toDomain(MealAnalysisJpaEntity entity) {
        return entity.toDomain();
    }
}
