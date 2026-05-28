package com.ssambbong.gymjjak.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplicationStatus;
import com.ssambbong.gymjjak.organization.domain.repository.OrganizationApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrganizationApplicationAdaptor implements OrganizationApplicationRepository {

    private final SpringDataOrganizationApplicationRepository springDataOrganizationApplicationRepository;

    @Override
    public boolean existsByBusinessRegistrationNumberAndStatus(String businessRegistrationNumber) {
       boolean alreadyExist = springDataOrganizationApplicationRepository
               .existsByBusinessRegistrationNumberAndStatus(
                       businessRegistrationNumber,
                       OrganizationApplicationStatus.ACCEPTED);

       return alreadyExist;
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
}
