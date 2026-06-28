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

    boolean existsByOrganizationIdAndTrainerProfileIdAndRemovedAtIsNull(Long organizationId, Long trainerProfileId);

    long countByRemovedAtIsNull();

    @Query("SELECT COUNT(DISTINCT e.organizationId) FROM OrganizationTrainerJpaEntity e WHERE e.removedAt IS NULL")
    long countDistinctOrganizationsWithActiveTrainers();

    @Query(value = """
            SELECT ot.organization_trainer_id,
                   ot.trainer_profile_id,
                   u.username,
                   u.nickname,
                   tp.trainer_name,
                   ot.registered_at
            FROM organization_trainers ot
            JOIN trainer_profiles tp ON ot.trainer_profile_id = tp.trainer_profile_id
            JOIN users u ON tp.user_id = u.user_id
            WHERE ot.organization_id = :organizationId
              AND ot.removed_at IS NULL
              AND tp.deleted_at IS NULL
            ORDER BY ot.registered_at ASC
            """, nativeQuery = true)
    List<TrainerSummaryRow> findTrainerSummariesByOrganizationId(@Param("organizationId") Long organizationId);

    @Query(value = """
            SELECT tp.trainer_name,
                   tp.average_rating,
                   tp.review_count
            FROM organization_trainers ot
            JOIN trainer_profiles tp ON ot.trainer_profile_id = tp.trainer_profile_id
            WHERE ot.organization_id = :organizationId
              AND ot.removed_at IS NULL
              AND tp.deleted_at IS NULL
            ORDER BY ot.registered_at ASC
            """, nativeQuery = true)
    List<TrainerDetailRow> findTrainerDetailViewsByOrganizationId(@Param("organizationId") Long organizationId);

    @Query(value = """
            SELECT COUNT(DISTINCT pr.user_id)
            FROM pt_reservations pr
            WHERE pr.organization_id = :organizationId
              AND pr.cancelled_at IS NULL
            """, nativeQuery = true)
    long countAccumulatedMembersByOrganizationId(@Param("organizationId") Long organizationId);

    @Query(value = "SELECT organization_trainer_id FROM organization_trainers WHERE removed_at IS NOT NULL AND removed_at < :threshold LIMIT :batchSize", nativeQuery = true)
    List<Long> findHardDeleteCandidateIds(@Param("threshold") LocalDateTime threshold, @Param("batchSize") int batchSize);

    @Modifying
    @Query(value = "DELETE FROM organization_trainers WHERE organization_trainer_id IN :ids", nativeQuery = true)
    int hardDeleteByIds(@Param("ids") List<Long> ids);

}
