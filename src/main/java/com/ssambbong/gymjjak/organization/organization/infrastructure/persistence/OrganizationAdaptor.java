package com.ssambbong.gymjjak.organization.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organization.application.OrganizationAdminView;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListQuery;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListResult;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public OrganizationListResult findAllForAdmin(OrganizationListQuery query) {
        PageRequest pageRequest = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("organizationId"))
        );
        Page<OrganizationAdminView> page = springDataOrganizationRepository.findAllForAdmin(query.keyword(), pageRequest);
        return new OrganizationListResult(
                page.getContent(),
                query.page(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
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

    @Override
    public Optional<String> findRequestedLoginIdById(Long organizationId) {
        return springDataOrganizationRepository.findRequestedLoginIdById(organizationId);
    }


}
