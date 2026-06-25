package com.ssambbong.gymjjak.category.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

// domain/application은 이 인터페이스를 알지 못함
public interface SpringDataCategoryRepository extends JpaRepository<CategoryJpaEntity, Long> {

    boolean existsByNameAndDeletedAtIsNull(String name);

    java.util.List<CategoryJpaEntity> findAllByDeletedAtIsNull();

    @Query(value = "SELECT COUNT(*) FROM pt_courses WHERE category_id = :categoryId AND deleted_at IS NULL",
            nativeQuery = true)
    long countPtCoursesByCategoryId(@Param("categoryId") Long categoryId);

    Optional<CategoryJpaEntity> findByNameAndDeletedAtIsNull(String name);

    Optional<CategoryJpaEntity> findByName(String name);

    // hard delete 될 목록 조회
    @Query(value = "SELECT category_id FROM categories WHERE deleted_at IS NOT NULL AND deleted_at < :threshold LIMIT :batchSize",
            nativeQuery = true)
    List<Long> findHardDeleteCandidateIds(@Param("threshold") LocalDateTime threshold, @Param("batchSize") int batchSize);

    // hard delete
    @Modifying
    @Query(value = "DELETE FROM categories WHERE category_id IN :ids", nativeQuery = true)
    int hardDeleteByIds(@Param("ids") List<Long> ids);
}
