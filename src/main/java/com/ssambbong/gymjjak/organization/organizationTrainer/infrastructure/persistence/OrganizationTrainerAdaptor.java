package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organizationTrainer.domain.model.OrganizationTrainer;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
