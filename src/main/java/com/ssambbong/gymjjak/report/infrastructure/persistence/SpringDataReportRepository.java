package com.ssambbong.gymjjak.report.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataReportRepository extends JpaRepository<ReportJpaEntity, Long> {

    // 최신 신고일 조회
    Optional<ReportJpaEntity> findTopByReportGroupIdOrderByCreatedAtDesc(Long reportGroupId);
}
