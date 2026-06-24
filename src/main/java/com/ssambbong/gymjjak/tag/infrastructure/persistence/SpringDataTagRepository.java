package com.ssambbong.gymjjak.tag.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataTagRepository extends JpaRepository<TagJpaEntity, Long> {

    boolean existsByNameAndDeletedAtIsNull(String name);

    List<TagJpaEntity> findAllByDeletedAtIsNull();

    @Query(value = "SELECT COUNT(*) FROM pt_courses WHERE tag_id = :tagId AND deleted_at IS NULL",
            nativeQuery = true)
    long countPtCoursesByTagId(@Param("tagId") Long tagId);
}
