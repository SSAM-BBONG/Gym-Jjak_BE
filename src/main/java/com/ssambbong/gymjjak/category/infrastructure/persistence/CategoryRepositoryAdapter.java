package com.ssambbong.gymjjak.category.infrastructure.persistence;

import com.ssambbong.gymjjak.calendar.domain.exception.CalendarErrorCode;
import com.ssambbong.gymjjak.calendar.domain.exception.CalendarException;
import com.ssambbong.gymjjak.category.domain.exception.CategoryErrorCode;
import com.ssambbong.gymjjak.category.domain.exception.CategoryNotFoundException;
import com.ssambbong.gymjjak.category.domain.model.Category;
import com.ssambbong.gymjjak.category.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// CategoryRepo(도메인 인터페이스 Port)의 jpa 구현체
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

    // JPA 기술 구현체 주입
    private final SpringDataCategoryRepository repository;

    // 등록, 수정
    @Override
    public Category save(Category category) {
        // id가 null이면 새 카테고리 등록, 있으면 기존 카테고리 수정
        CategoryJpaEntity entity = category.getId() == null
                ? new CategoryJpaEntity(category.getName())
                : repository.findById(category.getId())
                .orElseThrow();
        // 이름 변경
        entity.changeName(category.getName());
        // 저장 후 domain으로 변환하여 반환
        return repository.save(entity).toDomain();
    }

    // 단건 조회
    @Override
    public Optional<Category> findById(Long id) {
        // JpaEntity -> domain으로 변환하여 반환
        return repository.findById(id).map(CategoryJpaEntity::toDomain);
    }

    // 목록 조회 (삭제되지 않은 것만)
    @Override
    public List<Category> findAll() {
        return repository.findAllByDeletedAtIsNull()
                .stream()
                .map(CategoryJpaEntity::toDomain)
                .toList();
    }

    // 소프트 딜리트
    @Override
    public void deleteById(Long id) {
        repository.findById(id).ifPresent(entity -> {
            entity.softDelete();
            repository.save(entity);
        });
    }

    // 중복 여부 (삭제되지 않은 것만)
    @Override
    public boolean existsByName(String name) {
        return repository.existsByNameAndDeletedAtIsNull(name);
    }

    // PT 강습 사용 개수
    @Override
    public long countPtCoursesByCategoryId(Long categoryId) {
        return repository.countPtCoursesByCategoryId(categoryId);
    }


}
