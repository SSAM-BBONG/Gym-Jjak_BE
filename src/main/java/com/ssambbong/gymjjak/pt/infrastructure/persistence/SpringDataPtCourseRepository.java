package com.ssambbong.gymjjak.pt.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataPtCourseRepository extends JpaRepository<PtCourseJpaEntity, Long> {

    // 최신순 전체 목록 (기존 유지)
    List<PtCourseJpaEntity> findAllByOrderByCreatedAtDesc();

    // VISIBLE 상태 페이지네이션
    Page<PtCourseJpaEntity> findAllByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
            PtCourseStatus status, Pageable pageable);
}
