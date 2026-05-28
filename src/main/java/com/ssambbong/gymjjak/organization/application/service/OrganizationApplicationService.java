package com.ssambbong.gymjjak.organization.application.service;

import com.ssambbong.gymjjak.organization.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.application.usecase.OrganizationApplicationUsecase;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.exception.DuplicateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationApplicationService implements OrganizationApplicationUsecase {

    private final OrganizationApplicationRepository organizationApplicationRepository;

    @Override
    @Transactional
    public Long createOrganizationApplication(OrganizationApplicationCreateCommand command) {

        boolean alreadyExist = organizationApplicationRepository.existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber());

        if (alreadyExist) {
            throw new DuplicateException();
        }

        OrganizationApplication organizationApplication = OrganizationApplication.create(
                command.applicantUserId(),
                command.requestedLoginId(),
                command.businessLicenseFileId(),
                command.businessRegistrationNumber(),
                command.businessName(),
                command.representativeName(),
                command.representativePhone(),
                command.openingDate(),
                command.roadAddress(),
                command.jibunAddress(),
                command.detailAddress(),
                command.latitude(),
                command.longitude(),
                command.websiteUrl(),
                command.instagramUrl(),
                command.blogUrl(),
                command.facilityPhone()
        );

        Long organizationApplicationId = organizationApplicationRepository.save(organizationApplication);

        return organizationApplicationId;
    }
}
