package com.ssambbong.gymjjak.organization.organizationApplication.application.service;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.organization.organizationApplication.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.organizationApplication.application.port.OrgApplicationMetricsPort;
import com.ssambbong.gymjjak.organization.organizationApplication.application.port.UserCreationPort;
import com.ssambbong.gymjjak.organization.organizationApplication.application.port.UserLoginIdValidationPort;
import com.ssambbong.gymjjak.organization.organizationApplication.application.usecase.OrganizationApplicationCommandUsecase;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.BusinessLicenseFileRegistrationFailedException;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.BusinessLicenseFileRequiredException;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.DuplicateBusinessRegistrationNumberException;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.DuplicateRequestedLoginIdException;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.OrganizationApplicationNotFoundException;
import com.ssambbong.gymjjak.organization.organization.application.port.OrganizationMetricsPort;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationApplicationCommandService implements OrganizationApplicationCommandUsecase {

    private final OrganizationApplicationRepository organizationApplicationRepository;
    private final OrganizationRepository organizationRepository;
    private final FileUseCase fileUseCase;
    private final UserCreationPort userCreationPort;
    private final UserLoginIdValidationPort userLoginIdValidationPort;
    private final OrgApplicationMetricsPort orgApplicationMetricsPort;
    private final OrganizationMetricsPort organizationMetricsPort;

    @Override
    @Transactional
    public Long createOrganizationApplication(OrganizationApplicationCreateCommand command) {

        if (command.businessLicenseFile() == null) {
            throw new BusinessLicenseFileRequiredException();
        }

        userLoginIdValidationPort.validate(command.requestedLoginId());

        boolean alreadyExist = organizationApplicationRepository.existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber());
        if (alreadyExist) {
            throw new DuplicateBusinessRegistrationNumberException();
        }

        boolean loginIdAlreadyExist = organizationApplicationRepository.existsByRequestedLoginId(command.requestedLoginId());
        if (loginIdAlreadyExist) {
            throw new DuplicateRequestedLoginIdException();
        }

        List<FileRegistrationResult> fileResults;
        try {
            fileResults = fileUseCase.registerFiles(List.of(
                    new CreateFileCommand(
                            command.applicantUserId(),
                            command.businessLicenseFile().fileKey(),
                            command.businessLicenseFile().originalName(),
                            command.businessLicenseFile().contentType(),
                            command.businessLicenseFile().fileSize(),
                            FileType.BUSINESS_LICENSE)
            ));
        } catch (RuntimeException e) {
            log.error("event=org_application_file_registration_failed", e);
            throw new BusinessLicenseFileRegistrationFailedException();
        }

        if (fileResults.isEmpty() || fileResults.get(0).fileId() == null) {
            throw new BusinessLicenseFileRegistrationFailedException();
        }

        Long fileId = fileResults.get(0).fileId();

        OrganizationApplication organizationApplication = OrganizationApplication.create(
                command.applicantUserId(),
                command.requestedLoginId(),
                fileId,
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
        recordMetricSafely(orgApplicationMetricsPort::recordOrgApplicationCreated, "recordOrgApplicationCreated");
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
        recordMetricSafely(organizationMetricsPort::recordOrganizationCreated, "recordOrganizationCreated");
        recordMetricSafely(orgApplicationMetricsPort::recordOrgApplicationApproved, "recordOrgApplicationApproved");
    }

    @Override
    @Transactional
    public void rejectOrganizationApplication(Long organizationApplicationId, Long reviewedBy, String rejectReason) {

        OrganizationApplication organizationApplication = organizationApplicationRepository
                .findById(organizationApplicationId)
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        OrganizationApplication rejected = organizationApplication.reject(reviewedBy, rejectReason);

        organizationApplicationRepository.reject(rejected);
        recordMetricSafely(orgApplicationMetricsPort::recordOrgApplicationRejected, "recordOrgApplicationRejected");
    }

    @Override
    @Transactional
    public void cancelOrganizationApplication(Long organizationApplicationId, Long applicantId) {

        OrganizationApplication organizationApplication = organizationApplicationRepository
                .findByIdAndApplicantUserId(organizationApplicationId, applicantId)
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        OrganizationApplication cancelled = organizationApplication.cancel();

        organizationApplicationRepository.cancel(cancelled);
        recordMetricSafely(orgApplicationMetricsPort::recordOrgApplicationCancelled, "recordOrgApplicationCancelled");
    }

    private void recordMetricSafely(Runnable metricCall, String metricName) {
        try {
            metricCall.run();
        } catch (Exception e) {
            log.warn("메트릭 기록 실패 - metric: {}", metricName, e);
        }
    }
}
