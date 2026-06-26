package com.ssambbong.gymjjak.category.infrastructure.persistence;

import com.ssambbong.gymjjak.category.domain.exception.CategoryNotFoundException;
import com.ssambbong.gymjjak.category.domain.model.Category;
import com.ssambbong.gymjjak.category.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final SpringDataCategoryRepository repository;

    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = category.getId() == null
                ? new CategoryJpaEntity(category.getName())
                : repository.findById(category.getId())
                .orElseThrow();
        entity.changeName(category.getName());
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return repository.findById(id).map(CategoryJpaEntity::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll()
                .stream()
                .map(CategoryJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public long countPtCoursesByCategoryId(Long categoryId) {
        return repository.countPtCoursesByCategoryId(categoryId);
    }
}
