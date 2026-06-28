package com.ssambbong.gymjjak.organization.organization.presentation.api.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.organization.organization.application.OrganizationAdminView;
import com.ssambbong.gymjjak.organization.organization.application.query.MyOrganizationResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationAdminDetailResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationDetailResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListResult;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindMyOrganizationResponse;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindOrganizationAdminDetailResponse;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindOrganizationDetailResponse;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindOrganizationResponse;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindOrganizationsListResponse;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindOrganizationsResponse;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.AdminTrainerSummary;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerDetailView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface OrganizationMapper {

    FindOrganizationResponse toResponse(Organization organization);

    FindOrganizationsResponse toResponse(OrganizationAdminView view);

    @Mapping(source = "items", target = "organizations")
    FindOrganizationsListResponse toListResponse(OrganizationListResult result);

    FindMyOrganizationResponse toMyOrganizationResponse(MyOrganizationResult result, String businessLicenseFileUrl, String businessLicenseOriginalName);

    FindOrganizationDetailResponse toDetailResponse(OrganizationDetailResult result);

    FindOrganizationDetailResponse.TrainerSummary toTrainerSummary(TrainerDetailView view);

    FindOrganizationAdminDetailResponse toAdminDetailResponse(OrganizationAdminDetailResult result, String businessLicenseFileUrl, String businessLicenseOriginalName);

    FindOrganizationAdminDetailResponse.TrainerInfo toAdminTrainerInfo(AdminTrainerSummary summary);
}
