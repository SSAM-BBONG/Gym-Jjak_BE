package com.ssambbong.gymjjak.organization.application.service;

import com.ssambbong.gymjjak.organization.application.usecase.OrganizationApplicationQueryUsecase;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplicationStatus;
import com.ssambbong.gymjjak.organization.domain.repository.OrganizationApplicationRepository;
import com.ssambbong.gymjjak.organization.exception.OrganizationApplicationAccessDeniedException;
import com.ssambbong.gymjjak.organization.exception.OrganizationApplicationNotFoundException;
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

        OrganizationApplication organizationApplication = organizationApplicationRepository.findById(organizationApplicationId)
                .orElseThrow(OrganizationApplicationNotFoundException::new);

        if (!isAdmin && !organizationApplication.getApplicantUserId().equals(requestUserId)) {
            throw new OrganizationApplicationAccessDeniedException();
        }

        return organizationApplication;
    }

    @Override
    public List<OrganizationApplication> findPendingOrganizationApplications() {
        return organizationApplicationRepository.findAllByStatus(OrganizationApplicationStatus.PENDING);
    }

}
