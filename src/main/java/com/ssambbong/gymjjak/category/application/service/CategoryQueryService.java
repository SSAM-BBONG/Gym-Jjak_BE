package com.ssambbong.gymjjak.category.application.service;

import com.ssambbong.gymjjak.category.application.usecase.CategoryQueryUseCase;
import com.ssambbong.gymjjak.category.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryQueryService implements CategoryQueryUseCase {

    private final CategoryRepository categoryRepository;

    public CategoryQueryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryView> handle() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryView(
                        category.getId(),
                        category.getName(),
                        category.getCreatedAt(),
                        categoryRepository.countPtCoursesByCategoryId(category.getId())
                ))
                .toList();
    }
}
