package com.ssambbong.gymjjak.organization.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationAdminView;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataOrganizationRepository extends JpaRepository<OrganizationJpaEntity, Long> {

    Optional<OrganizationJpaEntity> findByOrganizationAccountId(Long organizationAccountId);

    long countByStatus(OrganizationStatus status);

    boolean existsByOrganizationIdAndStatus(
            Long organizationId,
            OrganizationStatus status
    );

    // 활성화상태의 조직 계정 조회
    @Query("""
            SELECT o.organizationId
            FROM OrganizationJpaEntity o
            WHERE o.organizationAccountId = :organizationAccountId
              AND o.status = :status
            """)
    Optional<Long> findIdByOrganizationAccountIdAndStatus(
            @Param("organizationAccountId") Long organizationAccountId,
            @Param("status") OrganizationStatus status
    );

    @Query(
            value = """
                    SELECT new com.ssambbong.gymjjak.organization.organization.application.query.OrganizationAdminView(
                        o.organizationId, a.requestedLoginId, o.businessName, o.representativeName,
                        o.representativePhone, COUNT(t), o.status, o.createdAt
                    )
                    FROM OrganizationJpaEntity o
                    LEFT JOIN OrganizationApplicationJpaEntity a ON a.organizationApplicationId = o.applicationId
                    LEFT JOIN OrganizationTrainerJpaEntity t ON t.organizationId = o.organizationId AND t.removedAt IS NULL
                    WHERE :keyword IS NULL
                       OR a.requestedLoginId LIKE CONCAT('%', :keyword, '%')
                       OR o.businessName LIKE CONCAT('%', :keyword, '%')
                       OR o.representativeName LIKE CONCAT('%', :keyword, '%')
                    GROUP BY o.organizationId, a.requestedLoginId, o.businessName, o.representativeName,
                             o.representativePhone, o.status, o.createdAt
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT o.organizationId)
                    FROM OrganizationJpaEntity o
                    LEFT JOIN OrganizationApplicationJpaEntity a ON a.organizationApplicationId = o.applicationId
                    WHERE :keyword IS NULL
                       OR a.requestedLoginId LIKE CONCAT('%', :keyword, '%')
                       OR o.businessName LIKE CONCAT('%', :keyword, '%')
                       OR o.representativeName LIKE CONCAT('%', :keyword, '%')
                    """
    )
    Page<OrganizationAdminView> findAllForAdmin(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT a.requestedLoginId FROM OrganizationJpaEntity o JOIN o.application a WHERE o.organizationId = :organizationId")
    Optional<String> findRequestedLoginIdById(@Param("organizationId") Long organizationId);

    @Query("SELECT o FROM OrganizationJpaEntity o JOIN FETCH o.application WHERE o.organizationAccountId = :accountId")
    Optional<OrganizationJpaEntity> findByOrganizationAccountIdWithApplication(@Param("accountId") Long accountId);

    @Query(value = "SELECT organization_id FROM organizations WHERE deleted_at IS NOT NULL AND deleted_at < :threshold ORDER BY deleted_at ASC, organization_id ASC LIMIT :batchSize", nativeQuery = true)
    List<Long> findHardDeleteCandidateIds(@Param("threshold") LocalDateTime threshold, @Param("batchSize") int batchSize);

    @Query(value = "SELECT application_id FROM organizations WHERE organization_id IN :ids", nativeQuery = true)
    List<Long> findApplicationIdsByOrganizationIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "DELETE FROM organizations WHERE organization_id IN :ids", nativeQuery = true)
    int hardDeleteByIds(@Param("ids") List<Long> ids);

    @Query(
            value = """
                    SELECT o.organizationId AS organizationId,
                           o.businessName AS businessName,
                           o.representativeName AS representativeName,
                           o.roadAddress AS roadAddress,
                           o.detailAddress AS detailAddress
                    FROM OrganizationJpaEntity o
                    WHERE o.status = :status
                      AND (:keyword IS NULL
                           OR o.businessName LIKE CONCAT('%', :keyword, '%')
                           OR o.representativeName LIKE CONCAT('%', :keyword, '%'))
                    ORDER BY o.businessName ASC, o.organizationId ASC
                    """,
            countQuery = """
                    SELECT COUNT(o) FROM OrganizationJpaEntity o
                    WHERE o.status = :status
                      AND (:keyword IS NULL
                           OR o.businessName LIKE CONCAT('%', :keyword, '%')
                           OR o.representativeName LIKE CONCAT('%', :keyword, '%'))
                    """
    )
    Page<OrganizationSearchView> searchByKeyword(
            @Param("keyword") String keyword,
            @Param("status") OrganizationStatus status,
            Pageable pageable
    );

    interface OrganizationSearchView {
        Long getOrganizationId();
        String getBusinessName();
        String getRepresentativeName();
        String getRoadAddress();
        String getDetailAddress();
    }
}
