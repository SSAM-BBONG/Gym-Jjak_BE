package com.ssambbong.gymjjak.organization.application.service;

import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.organization.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.application.usecase.OrganizationApplicationCommandUsecase;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.exception.DuplicateBusinessRegistrationNumberException;
import com.ssambbong.gymjjak.organization.exception.DuplicateRequestedLoginIdException;
import com.ssambbong.gymjjak.organization.exception.OrganizationApplicationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationApplicationCommandService implements OrganizationApplicationCommandUsecase {

    private final OrganizationApplicationRepository organizationApplicationRepository;
    private final FileUseCase fileUseCase;

    @Override
    @Transactional
    public Long createOrganizationApplication(MultipartFile businessLicenseFile, OrganizationApplicationCreateCommand command) {

        boolean alreadyExist = organizationApplicationRepository.existsByBusinessRegistrationNumberAndStatus(command.businessRegistrationNumber());
        if (alreadyExist) {
            throw new DuplicateBusinessRegistrationNumberException();
        }

        boolean loginIdAlreadyExist = organizationApplicationRepository.existsByRequestedLoginId(command.requestedLoginId());
        if (loginIdAlreadyExist) {
            throw new DuplicateRequestedLoginIdException();
        }

        Long fileId = fileUseCase.uploadFile(businessLicenseFile, command.applicantUserId(), FileType.BUSINESS_LICENSE);

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

        try {
            return organizationApplicationRepository.save(organizationApplication);
        } catch (DataAccessException e) {
            log.error("조직 신청 DB 저장 실패 → S3 파일 롤백 - fileId: {}", fileId);
            fileUseCase.deleteFromStorage(fileId); // S3만 삭제, DB는 트랜잭션 롤백이 처리
            throw e;
        }
    }

    @Override
    @Transactional
    public void approveOrganizationApplication(Long organizationApplicationId, Long reviewedBy) {

        OrganizationApplication organizationApplication = organizationApplicationRepository
                .findById(organizationApplicationId)
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        OrganizationApplication approved = organizationApplication.approve(reviewedBy);

        organizationApplicationRepository.approve(approved);
    }

    @Override
    @Transactional
    public void rejectOrganizationApplication(Long organizationApplicationId, Long reviewedBy, String rejectReason) {

        OrganizationApplication organizationApplication = organizationApplicationRepository
                .findById(organizationApplicationId)
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        OrganizationApplication rejected = organizationApplication.reject(reviewedBy, rejectReason);

        organizationApplicationRepository.reject(rejected);
    }

    @Override
    @Transactional
    public void cancelOrganizationApplication(Long organizationApplicationId, Long applicantId) {

        OrganizationApplication organizationApplication = organizationApplicationRepository
                .findByIdAndApplicantUserId(organizationApplicationId, applicantId)
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        OrganizationApplication cancelled = organizationApplication.cancel();

        organizationApplicationRepository.cancel(cancelled);
    }
}
