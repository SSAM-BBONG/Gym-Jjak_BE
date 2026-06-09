package com.ssambbong.gymjjak.pt.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.domain.model.PtCourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataPtCourseRepository extends JpaRepository<PtCourseJpaEntity, Long> {

    // VISIBLE 상태 전체 목록 최신순
    List<PtCourseJpaEntity> findAllByStatusAndDeletedAtIsNullOrderByCreatedAtDesc(PtCourseStatus status);
}
