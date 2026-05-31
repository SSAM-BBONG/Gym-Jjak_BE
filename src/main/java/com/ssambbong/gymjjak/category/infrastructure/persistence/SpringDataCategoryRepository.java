package com.ssambbong.gymjjak.category.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// domain/application은 이 인터페이스를 알지 못함
public interface SpringDataCategoryRepository extends JpaRepository<CategoryJpaEntity, Long> {

    boolean existsByNameAndDeletedAtIsNull(String name);

    java.util.List<CategoryJpaEntity> findAllByDeletedAtIsNull();

    @Query(value = "SELECT COUNT(*) FROM pt_courses WHERE category_id = :categoryId AND deleted_at IS NULL",
            nativeQuery = true)
    long countPtCoursesByCategoryId(@Param("categoryId") Long categoryId);
}
