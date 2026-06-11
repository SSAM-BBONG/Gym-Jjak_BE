package com.ssambbong.gymjjak.organization.organizationApplication.application.service;

import com.ssambbong.gymjjak.organization.organizationApplication.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.organizationApplication.application.port.OrgApplicationMetricsPort;
import com.ssambbong.gymjjak.organization.organizationApplication.application.port.UserCreationPort;
import com.ssambbong.gymjjak.organization.organizationApplication.application.usecase.OrganizationApplicationCommandUsecase;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.DuplicateBusinessRegistrationNumberException;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.DuplicateRequestedLoginIdException;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.OrganizationApplicationNotFoundException;
import com.ssambbong.gymjjak.organization.organization.application.port.OrganizationMetricsPort;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationApplicationCommandService implements OrganizationApplicationCommandUsecase {

    private final OrganizationApplicationRepository organizationApplicationRepository;
    private final OrganizationRepository organizationRepository;
    private final UserCreationPort userCreationPort;
    private final OrgApplicationMetricsPort orgApplicationMetricsPort;
    private final OrganizationMetricsPort organizationMetricsPort;

    @Override
    @Transactional
    public Long createOrganizationApplication(OrganizationApplicationCreateCommand command) {

        boolean alreadyExist = organizationApplicationRepository.existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber());
        if (alreadyExist) {
            throw new DuplicateBusinessRegistrationNumberException();
        }

        boolean loginIdAlreadyExist = organizationApplicationRepository.existsByRequestedLoginId(command.requestedLoginId());
        if (loginIdAlreadyExist) {
            throw new DuplicateRequestedLoginIdException();
        }

        OrganizationApplication organizationApplication = OrganizationApplication.create(
                command.applicantUserId(),
                command.requestedLoginId(),
                command.fileId(),
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

        Long applicationId = organizationApplicationRepository.save(organizationApplication);
        orgApplicationMetricsPort.recordOrgApplicationCreated();
        return applicationId;
    }

    @Override
    @Transactional
    public void approveOrganizationApplication(Long organizationApplicationId, Long reviewedBy) {

        OrganizationApplication organizationApplication = organizationApplicationRepository
                .findById(organizationApplicationId)
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        OrganizationApplication approved = organizationApplication.approve(reviewedBy);
        organizationApplicationRepository.approve(approved);

        Long organizationAccountId = userCreationPort.createOrganizationAccount(
                approved.getRequestedLoginId(),
                approved.getRepresentativeName(),
                approved.getBusinessName(),
                approved.getRepresentativePhone()
        );

        Organization organization = Organization.create(organizationAccountId, approved);
        organizationRepository.save(organization);
        organizationMetricsPort.recordOrganizationCreated();

        orgApplicationMetricsPort.recordOrgApplicationApproved();
    }

    @Override
    @Transactional
    public void rejectOrganizationApplication(Long organizationApplicationId, Long reviewedBy, String rejectReason) {

        OrganizationApplication organizationApplication = organizationApplicationRepository
                .findById(organizationApplicationId)
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        OrganizationApplication rejected = organizationApplication.reject(reviewedBy, rejectReason);

        organizationApplicationRepository.reject(rejected);
        orgApplicationMetricsPort.recordOrgApplicationRejected();
    }

    @Override
    @Transactional
    public void cancelOrganizationApplication(Long organizationApplicationId, Long applicantId) {

        OrganizationApplication organizationApplication = organizationApplicationRepository
                .findByIdAndApplicantUserId(organizationApplicationId, applicantId)
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        OrganizationApplication cancelled = organizationApplication.cancel();

        organizationApplicationRepository.cancel(cancelled);
        orgApplicationMetricsPort.recordOrgApplicationCancelled();
    }
}
