package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataOrganizationTrainerRepository extends JpaRepository<OrganizationTrainerJpaEntity, Long> {

    List<OrganizationTrainerJpaEntity> findByOrganizationIdAndRemovedAtIsNull(Long organizationId);

    long countByOrganizationIdAndRemovedAtIsNull(Long organizationId);
}
