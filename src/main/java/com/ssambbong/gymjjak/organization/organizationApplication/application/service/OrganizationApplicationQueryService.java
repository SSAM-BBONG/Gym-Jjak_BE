package com.ssambbong.gymjjak.organization.organizationApplication.application.service;

import com.ssambbong.gymjjak.organization.organizationApplication.application.port.UserLoginIdValidationPort;
import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListQuery;
import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListResult;
import com.ssambbong.gymjjak.organization.organizationApplication.application.usecase.OrganizationApplicationQueryUsecase;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplicationStatus;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.DuplicateRequestedLoginIdException;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.OrganizationApplicationNotFoundException;
import com.ssambbong.gymjjak.global.infrastructure.aop.Monitored;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrganizationApplicationQueryService implements OrganizationApplicationQueryUsecase {

    private final OrganizationApplicationRepository organizationApplicationRepository;
    private final UserLoginIdValidationPort userLoginIdValidationPort;

    @Monitored(name = "gymjjak.org.application.query.duration", domain = "org_application", action = "find_my")
    @Override
    public List<OrganizationApplication> findMyOrganizationApplications(Long applicantUserId) {
        return organizationApplicationRepository.findAllByApplicantUserId(applicantUserId);
    }

    @Override
    public OrganizationApplication findOrganizationApplicationDetails(Long organizationApplicationId, Long requestUserId, boolean isAdmin) {

        if (isAdmin) {
            return organizationApplicationRepository.findById(organizationApplicationId)
                    .orElseThrow(OrganizationApplicationNotFoundException::new);
        }

        return organizationApplicationRepository.findByIdAndApplicantUserId(organizationApplicationId, requestUserId)
                .orElseThrow(OrganizationApplicationNotFoundException::new);
    }

    @Monitored(name = "gymjjak.org.application.query.duration", domain = "org_application", action = "find_pending")
    @Override
    public ApplicationListResult findPendingOrganizationApplications(ApplicationListQuery query) {
        return organizationApplicationRepository.findAllByStatus(OrganizationApplicationStatus.PENDING, query);
    }

    @Override
    public void checkLoginIdDuplicate(String requestedLoginId) {
        userLoginIdValidationPort.validate(requestedLoginId);

        if (organizationApplicationRepository.existsByRequestedLoginId(requestedLoginId)) {
            throw new DuplicateRequestedLoginIdException();
        }
    }

}
