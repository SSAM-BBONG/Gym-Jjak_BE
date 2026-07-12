package com.ssambbong.gymjjak.organization.organization.application.service;

import com.ssambbong.gymjjak.organization.organization.application.query.MyOrganizationResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationAdminDetailResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationDetailResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListQuery;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationSearchListResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationSearchQuery;
import com.ssambbong.gymjjak.organization.organization.application.usecase.OrganizationQueryUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.AdminTrainerSummary;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerDetailView;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
import com.ssambbong.gymjjak.global.infrastructure.aop.Monitored;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationQueryService implements OrganizationQueryUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationTrainerRepository organizationTrainerRepository;

    @Monitored(name = "gymjjak.org.query.duration", domain = "organization", action = "find_my")
    @Override
    public MyOrganizationResult findMyOrganization(Long organizationAccountId) {
        return organizationRepository.findMyOrganizationByAccountId(organizationAccountId)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    @Monitored(name = "gymjjak.org.query.duration", domain = "organization", action = "find_all")
    @Override
    public OrganizationListResult findOrganizations(OrganizationListQuery query) {
        return organizationRepository.findAllForAdmin(query);
    }

    @Monitored(name = "gymjjak.org.query.duration", domain = "organization", action = "find_admin_detail")
    @Override
    public OrganizationAdminDetailResult findOrganizationAdminDetail(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        String requestedLoginId = organizationRepository.findRequestedLoginIdById(organizationId)
                .orElse(null);

        List<AdminTrainerSummary> trainers = organizationTrainerRepository.findAdminTrainersByOrganizationId(organizationId);

        return new OrganizationAdminDetailResult(
                organization.getOrganizationId(),
                requestedLoginId,
                organization.getBusinessLicenseFileId(),
                organization.getBusinessRegistrationNumber(),
                organization.getBusinessName(),
                organization.getRepresentativeName(),
                organization.getRepresentativePhone(),
                organization.getOpeningDate(),
                organization.getRoadAddress(),
                organization.getDetailAddress(),
                organization.getLatitude(),
                organization.getLongitude(),
                organization.getWebsiteUrl(),
                organization.getInstagramUrl(),
                organization.getBlogUrl(),
                organization.getFacilityPhone(),
                organization.getStatus(),
                organization.getCreatedAt(),
                trainers.size(),
                trainers
        );
    }

    @Override
    public OrganizationSearchListResult searchOrganizations(OrganizationSearchQuery query) {
        if (query.keyword() == null || query.keyword().isBlank()) {
            return new OrganizationSearchListResult(List.of(), query.page(), query.size(), 0L, 0);
        }
        return organizationRepository.searchOrganizations(
                new OrganizationSearchQuery(query.keyword().trim(), query.page(), query.size())
        );
    }

    @Monitored(name = "gymjjak.org.query.duration", domain = "organization", action = "find_detail")
    @Override
    public OrganizationDetailResult findOrganizationDetail(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        List<TrainerDetailView> trainers = organizationTrainerRepository.findTrainerDetailsByOrganizationId(organizationId);
        long accumulatedMembers = organizationTrainerRepository.countAccumulatedMembersByOrganizationId(organizationId);

        double avgRating = trainers.isEmpty() ? 0.0
                : trainers.stream().mapToDouble(TrainerDetailView::averageRating).average().orElse(0.0);

        return new OrganizationDetailResult(
                organization.getBusinessName(),
                organization.getRoadAddress(),
                organization.getDetailAddress(),
                organization.getFacilityPhone(),
                organization.getInstagramUrl(),
                organization.getBlogUrl(),
                organization.getWebsiteUrl(),
                trainers.size(),
                avgRating,
                accumulatedMembers,
                trainers
        );
    }
}
