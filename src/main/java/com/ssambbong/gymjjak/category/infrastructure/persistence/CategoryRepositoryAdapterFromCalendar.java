package com.ssambbong.gymjjak.category.infrastructure.persistence;

import com.ssambbong.gymjjak.calendar.application.port.out.WorkoutDiaryPortToCategory;
import com.ssambbong.gymjjak.category.domain.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapterFromCalendar implements WorkoutDiaryPortToCategory {

    private final SpringDataCategoryRepository repository;

    @Override
    public Long findCategoryIdByName(String categoryName) {
        return repository.findByName(categoryName)
                .map(CategoryJpaEntity::getId)
                .orElseThrow(CategoryNotFoundException::new);
    }
}
