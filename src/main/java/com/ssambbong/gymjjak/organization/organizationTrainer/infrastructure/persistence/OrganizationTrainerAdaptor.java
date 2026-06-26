package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.AdminTrainerSummary;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerDetailView;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerSummary;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.model.OrganizationTrainer;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.organization.organizationTrainer.exception.OrganizationTrainerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrganizationTrainerAdaptor implements OrganizationTrainerRepository {

    private final SpringDataOrganizationTrainerRepository springDataOrganizationTrainerRepository;

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
    public Optional<OrganizationTrainer> findActiveByIdAndOrganizationId(Long organizationTrainerId, Long organizationId) {
        return springDataOrganizationTrainerRepository
                .findByOrganizationTrainerIdAndOrganizationIdAndRemovedAtIsNull(organizationTrainerId, organizationId)
                .map(OrganizationTrainerJpaEntity::toDomain);
    }

    @Override
    public Long save(OrganizationTrainer organizationTrainer) {
        OrganizationTrainerJpaEntity entity = OrganizationTrainerJpaEntity.from(organizationTrainer);
        return springDataOrganizationTrainerRepository.save(entity).getOrganizationTrainerId();
    }

    @Override
    public boolean existsActiveByOrganizationIdAndTrainerProfileId(Long organizationId, Long trainerProfileId) {
        return springDataOrganizationTrainerRepository
                .existsByOrganizationIdAndTrainerProfileIdAndRemovedAtIsNull(organizationId, trainerProfileId);
    }

    @Override
    public void remove(Long organizationTrainerId) {
        int affected = springDataOrganizationTrainerRepository.markRemoved(organizationTrainerId, LocalDateTime.now());
        if (affected == 0) throw new OrganizationTrainerNotFoundException();
    }

    @Override
    public long countAllActive() {
        return springDataOrganizationTrainerRepository.countByRemovedAtIsNull();
    }

    @Override
    public long countActiveOrganizations() {
        return springDataOrganizationTrainerRepository.countDistinctOrganizationsWithActiveTrainers();
    }

    @Override
    public List<TrainerDetailView> findTrainerDetailsByOrganizationId(Long organizationId) {
        return springDataOrganizationTrainerRepository
                .findTrainerDetailViewsByOrganizationId(organizationId)
                .stream()
                .map(r -> new TrainerDetailView(
                        r.getTrainerName(),
                        r.getAverageRating(),
                        r.getReviewCount()
                ))
                .toList();
    }

    @Override
    public long countAccumulatedMembersByOrganizationId(Long organizationId) {
        return springDataOrganizationTrainerRepository.countAccumulatedMembersByOrganizationId(organizationId);
    }

    @Override
    public List<TrainerSummary> findTrainersByOrganizationId(Long organizationId) {
        return springDataOrganizationTrainerRepository
                .findTrainerSummariesByOrganizationId(organizationId)
                .stream()
                .map(r -> new TrainerSummary(
                        r.getOrganizationTrainerId(),
                        r.getTrainerProfileId(),
                        r.getUsername(),
                        r.getTrainerName(),
                        r.getRegisteredAt()
                ))
                .toList();
    }

    @Override
    public List<AdminTrainerSummary> findAdminTrainersByOrganizationId(Long organizationId) {
        return springDataOrganizationTrainerRepository
                .findTrainerSummariesByOrganizationId(organizationId)
                .stream()
                .map(r -> new AdminTrainerSummary(
                        r.getOrganizationTrainerId(),
                        r.getTrainerProfileId(),
                        r.getTrainerName(),
                        r.getUsername(),
                        r.getRegisteredAt()
                ))
                .toList();
    }
}
