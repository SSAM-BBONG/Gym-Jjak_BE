package com.ssambbong.gymjjak.organization.organization.presentation.api.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.organization.organization.application.OrganizationAdminView;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListResult;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindMyOrganizationResponse;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindOrganizationResponse;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindOrganizationsListResponse;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindOrganizationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface OrganizationMapper {

    FindOrganizationResponse toResponse(Organization organization);

    FindOrganizationsResponse toResponse(OrganizationAdminView view);

    @Mapping(source = "items", target = "organizations")
    FindOrganizationsListResponse toListResponse(OrganizationListResult result);

    @Mapping(target = "requestedLoginId", ignore = true)
    FindMyOrganizationResponse toMyOrganizationResponse(Organization organization, String businessLicenseFileUrl);
}
