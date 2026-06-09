package com.ssambbong.gymjjak.organization.organizationApplication.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataOrganizationApplicationRepository extends JpaRepository<OrganizationApplicationJpaEntity,Long> {

    boolean existsByBusinessRegistrationNumberAndStatusNotIn(String businessRegistrationNumber, List<OrganizationApplicationStatus> statuses);

    boolean existsByRequestedLoginIdAndStatusNotIn(String requestedLoginId, List<OrganizationApplicationStatus> statuses);

    List<OrganizationApplicationJpaEntity> findAllByApplicantUserId(Long applicantUserId);

    List<OrganizationApplicationJpaEntity> findAllByStatus(OrganizationApplicationStatus status);

    Optional<OrganizationApplicationJpaEntity> findByOrganizationApplicationIdAndApplicantUserId(Long organizationApplicationId, Long applicantId);

    @Query("SELECT o.requestedLoginId FROM OrganizationApplicationJpaEntity o WHERE o.organizationApplicationId = :applicationId")
    Optional<String> findRequestedLoginIdByOrganizationApplicationId(@Param("applicationId") Long applicationId);

    long countByStatus(OrganizationApplicationStatus status);
}
