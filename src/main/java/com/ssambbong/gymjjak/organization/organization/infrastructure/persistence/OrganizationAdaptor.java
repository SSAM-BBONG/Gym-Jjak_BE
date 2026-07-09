package com.ssambbong.gymjjak.organization.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationAdminView;
import com.ssambbong.gymjjak.organization.organization.application.query.MyOrganizationResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListQuery;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationSearchListResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationSearchResult;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
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
    public boolean existsByOrganizationIdAndStatus(
            Long organizationId,
            OrganizationStatus status
    ) {
        return springDataOrganizationRepository.existsByOrganizationIdAndStatus(
                organizationId,
                status
        );
    }

    @Override
    public Optional<Long> findIdByOrganizationAccountIdAndStatus(
            Long organizationAccountId,
            OrganizationStatus status
    ) {
        return springDataOrganizationRepository.findIdByOrganizationAccountIdAndStatus(
                organizationAccountId,
                status
        );
    }

    @Override
    public Optional<String> findRequestedLoginIdById(Long organizationId) {
        return springDataOrganizationRepository.findRequestedLoginIdById(organizationId);
    }

    @Override
    public List<Long> findHardDeleteCandidateIds(LocalDateTime threshold, int batchSize) {
        return springDataOrganizationRepository.findHardDeleteCandidateIds(threshold, batchSize);
    }

    @Override
    public List<Long> findApplicationIdsByOrganizationIds(List<Long> organizationIds) {
        return springDataOrganizationRepository.findApplicationIdsByOrganizationIds(organizationIds);
    }

    @Override
    public int hardDeleteByIds(List<Long> ids) {
        if (ids.isEmpty()) return 0;
        return springDataOrganizationRepository.hardDeleteByIds(ids);
    }

    @Override
    public OrganizationSearchListResult searchOrganizations(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrganizationJpaEntity> result = springDataOrganizationRepository.searchByKeyword(
                keyword, OrganizationStatus.ACTIVE, pageRequest);
        return new OrganizationSearchListResult(
                result.getContent().stream()
                        .map(e -> new OrganizationSearchResult(
                                e.getOrganizationId(),
                                e.getBusinessName(),
                                e.getRepresentativeName(),
                                e.getRoadAddress(),
                                e.getDetailAddress(),
                                e.getFacilityPhone()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public Optional<MyOrganizationResult> findMyOrganizationByAccountId(Long organizationAccountId) {
        return springDataOrganizationRepository.findByOrganizationAccountIdWithApplication(organizationAccountId)
                .map(e -> new MyOrganizationResult(
                        e.getBusinessLicenseFileId(),
                        e.getApplication().getRequestedLoginId(),
                        e.getBusinessRegistrationNumber(),
                        e.getBusinessName(),
                        e.getRepresentativeName(),
                        e.getRepresentativePhone(),
                        e.getOpeningDate(),
                        e.getRoadAddress(),
                        e.getJibunAddress(),
                        e.getDetailAddress(),
                        e.getLatitude(),
                        e.getLongitude(),
                        e.getFacilityPhone(),
                        e.getInstagramUrl(),
                        e.getBlogUrl(),
                        e.getWebsiteUrl()
                ));
    }
}
