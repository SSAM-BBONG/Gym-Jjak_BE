package com.ssambbong.gymjjak.pt.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataPtCourseRepository extends JpaRepository<PtCourseJpaEntity, Long> {

    // 최신순 정렬
    List<PtCourseJpaEntity> findAllByOrderByCreatedAtDesc();
}
