package com.ssambbong.gymjjak.organization.organization.application.service;

import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationDetailResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListQuery;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListResult;
import com.ssambbong.gymjjak.organization.organization.application.usecase.OrganizationQueryUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerDetailView;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository;
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

    @Override
    public Organization findMyOrganization(Long organizationAccountId) {
        return organizationRepository.findByOrganizationAccountId(organizationAccountId)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    @Override
    public OrganizationListResult findOrganizations(OrganizationListQuery query) {
        return organizationRepository.findAllForAdmin(query);
    }

    @Override
    public Organization findOrganizationById(Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);
    }

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
