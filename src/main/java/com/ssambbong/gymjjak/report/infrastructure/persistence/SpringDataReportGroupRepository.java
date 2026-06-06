package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataReportGroupRepository extends JpaRepository<ReportGroupJpaEntity, Long> {

    Page<ReportGroupJpaEntity> findByTargetTypeAndSanctionStatusNot(
            ReportTargetType targetType,
            ReportGroupSanctionStatus sanctionStatus,
            Pageable pageable
    );

    // 타겟 타입, 타겟 번호로 신고 그룹 조회
    Optional<ReportGroupJpaEntity> findByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);

    // 신고 번호 존재 여부 확인
    boolean existsByReportNumber(String reportNumber);

    long countByReviewStatusAndDeletedAtIsNull(ReportGroupReviewStatus reportGroupReviewStatus);
}
