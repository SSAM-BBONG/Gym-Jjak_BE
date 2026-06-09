package com.ssambbong.gymjjak.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataOrganizationRepository extends JpaRepository<OrganizationJpaEntity, Long> {

    long countByStatus(OrganizationStatus status);
}
