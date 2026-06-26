package com.ssambbong.gymjjak.tag.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataTagRepository extends JpaRepository<TagJpaEntity, Long> {

    boolean existsByName(String name);

    Optional<TagJpaEntity> findByName(String name);

    @Query(value = "SELECT COUNT(*) FROM pt_courses WHERE tag_id = :tagId AND deleted_at IS NULL",
            nativeQuery = true)
    long countPtCoursesByTagId(@Param("tagId") Long tagId);

    @Query(value = """
            SELECT pc.tag_id AS tagId, COUNT(*) AS cnt
            FROM pt_courses pc
            WHERE pc.tag_id IN (:tagIds) AND pc.deleted_at IS NULL
            GROUP BY pc.tag_id
            """, nativeQuery = true)
    List<Object[]> countPtCoursesByTagIds(@Param("tagIds") List<Long> tagIds);
}
