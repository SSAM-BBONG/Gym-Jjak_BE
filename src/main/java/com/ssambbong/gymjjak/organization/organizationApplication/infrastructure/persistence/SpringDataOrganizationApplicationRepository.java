package com.ssambbong.gymjjak.organization.organizationApplication.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataOrganizationApplicationRepository extends JpaRepository<OrganizationApplicationJpaEntity,Long> {

    boolean existsByBusinessRegistrationNumberAndStatusNotIn(String businessRegistrationNumber, List<OrganizationApplicationStatus> statuses);

    boolean existsByRequestedLoginIdAndStatusNotIn(String requestedLoginId, List<OrganizationApplicationStatus> statuses);

    List<OrganizationApplicationJpaEntity> findAllByApplicantUserId(Long applicantUserId);

    Page<OrganizationApplicationJpaEntity> findAllByStatus(OrganizationApplicationStatus status, Pageable pageable);

    Optional<OrganizationApplicationJpaEntity> findByOrganizationApplicationIdAndApplicantUserId(Long organizationApplicationId, Long applicantId);

    @Query("SELECT o.requestedLoginId FROM OrganizationApplicationJpaEntity o WHERE o.organizationApplicationId = :applicationId")
    Optional<String> findRequestedLoginIdByOrganizationApplicationId(@Param("applicationId") Long applicationId);

    long countByStatus(OrganizationApplicationStatus status);

    @Query(value = "SELECT organization_application_id FROM organization_applications WHERE deleted_at IS NOT NULL AND deleted_at < :threshold ORDER BY deleted_at ASC, organization_application_id ASC LIMIT :batchSize", nativeQuery = true)
    List<Long> findHardDeleteCandidateIds(@Param("threshold") LocalDateTime threshold, @Param("batchSize") int batchSize);

    @Modifying
    @Query(value = "DELETE FROM organization_applications WHERE organization_application_id IN :ids", nativeQuery = true)
    int hardDeleteByIds(@Param("ids") List<Long> ids);
}
