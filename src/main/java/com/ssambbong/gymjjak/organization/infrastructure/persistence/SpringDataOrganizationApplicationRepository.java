package com.ssambbong.gymjjak.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataOrganizationApplicationRepository extends JpaRepository<OrganizationApplicationJpaEntity,Long> {

    boolean existsByBusinessRegistrationNumberAndStatus(String businessRegistrationNumber, OrganizationApplicationStatus accepted);

    boolean existsByRequestedLoginId(String requestedLoginId);

    List<OrganizationApplicationJpaEntity> findAllByApplicantUserId(Long applicantUserId);

    List<OrganizationApplicationJpaEntity> findAllByStatus(OrganizationApplicationStatus status);

    Optional<OrganizationApplicationJpaEntity> findByOrganizationApplicationIdAndApplicantUserId(Long organizationApplicationId, Long applicantId);
}
