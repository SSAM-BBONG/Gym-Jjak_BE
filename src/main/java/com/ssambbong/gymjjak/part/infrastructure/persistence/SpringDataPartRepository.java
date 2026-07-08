package com.ssambbong.gymjjak.part.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataPartRepository extends JpaRepository<PartJpaEntity, Long> {

    boolean existsByName(String name);

    Optional<PartJpaEntity> findByName(String name);

    @Query(value = "SELECT COUNT(*) FROM pt_courses WHERE part_id = :partId AND deleted_at IS NULL",
            nativeQuery = true)
    long countPtCoursesByPartId(@Param("partId") Long partId);

    @Query(value = """
            SELECT pc.part_id AS partId, COUNT(*) AS cnt
            FROM pt_courses pc
            WHERE pc.part_id IN (:partIds) AND pc.deleted_at IS NULL
            GROUP BY pc.part_id
            """, nativeQuery = true)
    List<Object[]> countPtCoursesByPartIds(@Param("partIds") List<Long> partIds);
}
