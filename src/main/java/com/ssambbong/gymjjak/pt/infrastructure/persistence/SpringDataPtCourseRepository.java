package com.ssambbong.gymjjak.pt.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataPtCourseRepository extends JpaRepository<PtCourseJpaEntity, Long> {

    // 최신순 전체 목록 (기존 유지)
    List<PtCourseJpaEntity> findAllByOrderByCreatedAtDesc();

    // VISIBLE 상태 페이지네이션 (categoryId, tagId 선택 필터)
    @Query("SELECT pc FROM PtCourseJpaEntity pc " +
           "WHERE pc.status = 'VISIBLE' AND pc.deletedAt IS NULL " +
           "AND (:categoryId IS NULL OR pc.categoryId = :categoryId) " +
           "AND (:tagId IS NULL OR pc.tagId = :tagId) " +
           "ORDER BY pc.createdAt DESC")
    Page<PtCourseJpaEntity> findAllVisibleWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("tagId") Long tagId,
            Pageable pageable);
}
