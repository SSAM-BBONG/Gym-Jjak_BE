package com.ssambbong.gymjjak.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.domain.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrganizationApplicationRepository extends JpaRepository<OrganizationApplicationJpaEntity,Long> {

    boolean existsByBusinessRegistrationNumberAndStatus(String businessRegistrationNumber, Status accepted);
}
