package com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListResult;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.response.FindAllOrganizationApplicationsListResponse;
import com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.response.FindAllOrganizationApplicationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface OrganizationApplicationMapper {

    FindAllOrganizationApplicationsResponse toResponse(OrganizationApplication application);

    @Mapping(source = "items", target = "applications")
    FindAllOrganizationApplicationsListResponse toListResponse(ApplicationListResult result);
}
