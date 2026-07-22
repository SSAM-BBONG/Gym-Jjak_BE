package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.report.domain.model.ReportGroup;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    // 소프트 삭제된 신고 그룹 제외 후 단건 상세 조회합니다.
    Optional<ReportGroupJpaEntity> findByReportGroupIdAndDeletedAtIsNull(Long reportGroupId);

    long countByReviewStatusAndDeletedAtIsNull(ReportGroupReviewStatus reportGroupReviewStatus);

    long countBySanctionStatusAndDeletedAtIsNull(ReportGroupSanctionStatus reportGroupSanctionStatus);

    long countAllByDeletedAtIsNull();

    // soft delete 쿼리문
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE ReportGroupJpaEntity rg
        SET rg.deletedAt = :deletedAt
        WHERE rg.reportGroupId = :reportGroupId
          AND rg.reviewStatus = :reviewStatus
          AND rg.sanctionStatus = :sanctionStatus
          AND rg.deletedAt IS NULL
        """)
    int softDeleteResolvedManualBlindedById(
            @Param("reportGroupId") Long reportGroupId,
            @Param("reviewStatus") ReportGroupReviewStatus reviewStatus,
            @Param("sanctionStatus") ReportGroupSanctionStatus sanctionStatus,
            @Param("deletedAt") LocalDateTime deletedAt
    );


    // 수동 제재 + 처리 완료 + 수정일이 threshold 보다 오래된 신고 그룹 조회
    @Query("""
        SELECT rg.reportGroupId
        FROM ReportGroupJpaEntity rg
        WHERE rg.reviewStatus = :reviewStatus
          AND rg.sanctionStatus = :sanctionStatus
          AND rg.updatedAt < :threshold
          AND rg.deletedAt IS NOT NULL
        ORDER BY rg.reportGroupId ASC
        """)
    List<Long> findManualBlindedResolvedHardDeleteCandidateIds(
            @Param("reviewStatus") ReportGroupReviewStatus reportGroupReviewStatus,
            @Param("sanctionStatus") ReportGroupSanctionStatus reportGroupSanctionStatus,
            @Param("threshold") LocalDateTime threshold,
            PageRequest of);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM ReportGroupJpaEntity rg WHERE rg.reportGroupId IN :reportGroupIds")
    int hardDeleteByIds(@Param("reportGroupIds") List<Long> reportGroupIds);


}
