package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataOrganizationTrainerRepository extends JpaRepository<OrganizationTrainerJpaEntity, Long> {

    List<OrganizationTrainerJpaEntity> findByOrganizationIdAndRemovedAtIsNull(Long organizationId);

    long countByOrganizationIdAndRemovedAtIsNull(Long organizationId);

    Optional<OrganizationTrainerJpaEntity> findByOrganizationTrainerIdAndOrganizationIdAndRemovedAtIsNull(
            Long organizationTrainerId, Long organizationId);

    @Modifying
    @Query("UPDATE OrganizationTrainerJpaEntity e SET e.removedAt = :removedAt WHERE e.organizationTrainerId = :id AND e.removedAt IS NULL")
    int markRemoved(@Param("id") Long organizationTrainerId, @Param("removedAt") LocalDateTime removedAt);

    long countByRemovedAtIsNull();

    @Query("SELECT COUNT(DISTINCT e.organizationId) FROM OrganizationTrainerJpaEntity e WHERE e.removedAt IS NULL")
    long countDistinctOrganizationsWithActiveTrainers();
}
