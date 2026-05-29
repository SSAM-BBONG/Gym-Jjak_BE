package com.ssambbong.gymjjak.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplicationStatus;
import com.ssambbong.gymjjak.organization.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.exception.OrganizationApplicationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrganizationApplicationAdaptor implements OrganizationApplicationRepository {

    private final SpringDataOrganizationApplicationRepository springDataOrganizationApplicationRepository;

    @Override
    public boolean existsByBusinessRegistrationNumberAndStatus(String businessRegistrationNumber) {
       return springDataOrganizationApplicationRepository
               .existsByBusinessRegistrationNumberAndStatus(
                       businessRegistrationNumber,
                       OrganizationApplicationStatus.ACCEPTED);
    }

    @Override
    public boolean existsByRequestedLoginId(String requestedLoginId) {
        return springDataOrganizationApplicationRepository.existsByRequestedLoginId(requestedLoginId);
    }

    @Override
    public Long save(OrganizationApplication organizationApplication) {

        OrganizationApplicationJpaEntity organizationApplicationJpaEntity = OrganizationApplicationJpaEntity.fromDomain(organizationApplication);
        OrganizationApplicationJpaEntity saved = springDataOrganizationApplicationRepository.save(organizationApplicationJpaEntity);

        return saved.getOrganizationApplicationId();
    }

    @Override
    public List<OrganizationApplication> findAllByApplicantUserId(Long applicantUserId) {

        List<OrganizationApplicationJpaEntity> myOrganizationApplication =
                springDataOrganizationApplicationRepository.findAllByApplicantUserId(applicantUserId);

        return myOrganizationApplication.stream()
                .map(OrganizationApplicationJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<OrganizationApplication> findById(Long organizationApplicationId) {

        Optional<OrganizationApplicationJpaEntity> organizationApplicationDetails =
                springDataOrganizationApplicationRepository.findById(organizationApplicationId);

        return organizationApplicationDetails.map(OrganizationApplicationJpaEntity::toDomain);
    }

    @Override
    public List<OrganizationApplication> findAllByStatus(OrganizationApplicationStatus status) {

        return springDataOrganizationApplicationRepository.findAllByStatus(status).stream()
                .map(OrganizationApplicationJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void approve(OrganizationApplication organizationApplication) {

        OrganizationApplicationJpaEntity entity = springDataOrganizationApplicationRepository
                .findById(organizationApplication.getOrganizationApplicationId())
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        entity.approve(organizationApplication.getReviewedBy(), organizationApplication.getReviewedAt());
    }

    @Override
    public void reject(OrganizationApplication organizationApplication) {

        OrganizationApplicationJpaEntity entity = springDataOrganizationApplicationRepository
                .findById(organizationApplication.getOrganizationApplicationId())
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        entity.reject(organizationApplication.getReviewedBy(), organizationApplication.getReviewedAt(), organizationApplication.getRejectReason());
    }
}
