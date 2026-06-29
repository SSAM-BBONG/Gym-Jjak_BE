package com.ssambbong.gymjjak.organization.organizationApplication.domain.repository;

import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListQuery;
import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListResult;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrganizationApplicationRepository {

    boolean existsByBusinessRegistrationNumberAndStatus(String businessRegistrationNumber);

    boolean existsByRequestedLoginId(String requestedLoginId);

    Long save(OrganizationApplication organizationApplication);

    List<OrganizationApplication> findAllByApplicantUserId(Long applicantUserId);

    Optional<OrganizationApplication> findById(Long organizationApplicationId);

    ApplicationListResult findAllByStatus(OrganizationApplicationStatus status, ApplicationListQuery query);

    void approve(OrganizationApplication organizationApplication);

    void reject(OrganizationApplication organizationApplication);

    void cancel(OrganizationApplication organizationApplication);

    Optional<OrganizationApplication> findByIdAndApplicantUserId(Long organizationApplicationId, Long applicantId);

    Optional<String> findRequestedLoginIdByApplicationId(Long applicationId);

    long count();

    long countByStatus(OrganizationApplicationStatus status);

    List<Long> findHardDeleteCandidateIds(LocalDateTime threshold, int batchSize);

    int hardDeleteByIds(List<Long> ids);
}
