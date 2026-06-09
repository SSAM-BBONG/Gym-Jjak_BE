package com.ssambbong.gymjjak.organization.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrganizationAdaptor implements OrganizationRepository {

    private final SpringDataOrganizationRepository springDataOrganizationRepository;

    @Override
    public Long save(Organization organization) {
        OrganizationJpaEntity entity = OrganizationJpaEntity.fromDomain(organization);
        OrganizationJpaEntity saved = springDataOrganizationRepository.save(entity);
        return saved.getOrganizationId();
    }

    @Override
    public Optional<Organization> findById(Long organizationId) {
        return springDataOrganizationRepository.findById(organizationId)
                .map(OrganizationJpaEntity::toDomain);
    }

    @Override
    public Optional<Organization> findByOrganizationAccountId(Long organizationAccountId) {
        return springDataOrganizationRepository.findByOrganizationAccountId(organizationAccountId)
                .map(OrganizationJpaEntity::toDomain);
    }

    @Override
    public void update(Organization organization) {
        OrganizationJpaEntity entity = springDataOrganizationRepository.findById(organization.getOrganizationId())
                .orElseThrow(OrganizationNotFoundException::new);
        entity.update(organization);
    }

    @Override
    public long countByStatus(OrganizationStatus status) {
        return springDataOrganizationRepository.countByStatus(status);
    }

}
