package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataReportGroupRepository extends JpaRepository<ReportGroupJpaEntity, Long> {

    // TargetType으로 신고그룹 조회
    Page<ReportGroupJpaEntity> findByTargetType(ReportTargetType targetType, Pageable pageable);

}
