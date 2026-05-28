package com.ssambbong.gymjjak.organization.application.service;

import com.ssambbong.gymjjak.organization.application.usecase.OrganizationApplicationQueryUsecase;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.domain.repository.OrganizationApplicationRepository;
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

}
