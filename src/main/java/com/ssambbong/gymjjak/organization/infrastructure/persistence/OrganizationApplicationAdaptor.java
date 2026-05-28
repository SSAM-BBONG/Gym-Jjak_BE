package com.ssambbong.gymjjak.organization.infrastructure.persistence;

import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.model.Status;
import com.ssambbong.gymjjak.organization.domain.repository.OrganizationApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationApplicationAdaptor implements OrganizationApplicationRepository {

    private final SpringDataOrganizationApplicationRepository springDataOrganizationApplicationRepository;

    @Override
    public boolean existsByBusinessRegistrationNumberAndStatus(String businessRegistrationNumber) {
       boolean alreadyExist = springDataOrganizationApplicationRepository
               .existsByBusinessRegistrationNumberAndStatus(
                       businessRegistrationNumber,
                       Status.ACCEPTED);

       return alreadyExist;
    }

    @Override
    public Long save(OrganizationApplication organizationApplication) {

        OrganizationApplicationJpaEntity organizationApplicationJpaEntity = OrganizationApplicationJpaEntity.fromDomain(organizationApplication);
        OrganizationApplicationJpaEntity saved = springDataOrganizationApplicationRepository.save(organizationApplicationJpaEntity);

        return saved.getOrganizationApplicationId();
    }
}
