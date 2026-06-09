package com.ssambbong.gymjjak.organization.organizationApplication.application.service;

import com.ssambbong.gymjjak.organization.organizationApplication.application.usecase.OrganizationApplicationQueryUsecase;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplicationStatus;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.organizationApplication.exception.OrganizationApplicationNotFoundException;
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

    @Override
    public List<OrganizationApplication> findPendingOrganizationApplications() {
        return organizationApplicationRepository.findAllByStatus(OrganizationApplicationStatus.PENDING);
    }

}
