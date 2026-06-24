package com.ssambbong.gymjjak.tag.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataTagRepository extends JpaRepository<TagJpaEntity, Long> {

    boolean existsByNameAndDeletedAtIsNull(String name);

    List<TagJpaEntity> findAllByDeletedAtIsNull();

    Optional<TagJpaEntity> findByIdAndDeletedAtIsNull(Long id);

    @Query(value = "SELECT COUNT(*) FROM pt_courses WHERE tag_id = :tagId AND deleted_at IS NULL",
            nativeQuery = true)
    long countPtCoursesByTagId(@Param("tagId") Long tagId);

    @Query(value = """
            SELECT pc.tag_id AS tagId, COUNT(*) AS cnt
            FROM pt_courses pc
            WHERE pc.tag_id IN :tagIds AND pc.deleted_at IS NULL
            GROUP BY pc.tag_id
            """, nativeQuery = true)
    List<Object[]> countPtCoursesByTagIds(@Param("tagIds") List<Long> tagIds);

    @Modifying
    @Query(value = """
            UPDATE tags SET deleted_at = NOW(6)
            WHERE tag_id = :tagId
            AND deleted_at IS NULL
            AND NOT EXISTS (
                SELECT 1 FROM pt_courses WHERE tag_id = :tagId AND deleted_at IS NULL
            )
            """, nativeQuery = true)
    int softDeleteIfNotInUse(@Param("tagId") Long tagId);
}
