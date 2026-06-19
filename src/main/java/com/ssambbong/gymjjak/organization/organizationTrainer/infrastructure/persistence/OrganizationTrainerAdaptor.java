package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organizationTrainer.domain.model.OrganizationTrainer;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrganizationTrainerAdaptor implements OrganizationTrainerRepository {

    private final SpringDataOrganizationTrainerRepository springDataOrganizationTrainerRepository;
    private final EntityManager em;

    @Override
    public List<OrganizationTrainer> findActiveByOrganizationId(Long organizationId) {
        return springDataOrganizationTrainerRepository
                .findByOrganizationIdAndRemovedAtIsNull(organizationId)
                .stream()
                .map(OrganizationTrainerJpaEntity::toDomain)
                .toList();
    }

    @Override
    public long countActiveByOrganizationId(Long organizationId) {
        return springDataOrganizationTrainerRepository
                .countByOrganizationIdAndRemovedAtIsNull(organizationId);
    }

    @Override
    public List<TrainerSummary> findTrainersByOrganizationId(Long organizationId) {
        List<?> results = em.createNativeQuery("""
                SELECT ot.organization_trainer_id,
                       ot.trainer_profile_id,
                       u.username,
                       tp.trainer_name,
                       ot.registered_at
                FROM organization_trainers ot
                JOIN trainer_profiles tp ON ot.trainer_profile_id = tp.trainer_profile_id
                JOIN users u ON tp.user_id = u.user_id
                WHERE ot.organization_id = :organizationId
                  AND ot.removed_at IS NULL
                  AND tp.deleted_at IS NULL
                ORDER BY ot.registered_at ASC
                """)
                .setParameter("organizationId", organizationId)
                .getResultList();

        return results.stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new TrainerSummary(
                            ((Number) r[0]).longValue(),
                            ((Number) r[1]).longValue(),
                            (String) r[2],
                            (String) r[3],
                            r[4] != null ? ((java.sql.Timestamp) r[4]).toLocalDateTime() : null
                    );
                })
                .toList();
    }
}
