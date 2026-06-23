package com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.organization.organizationApplication.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.organizationApplication.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListResult;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.request.OrganizationApplicationCreateRequest;
import com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.response.FindAllOrganizationApplicationsListResponse;
import com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.response.FindAllOrganizationApplicationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface OrganizationApplicationMapper {

    UploadedFileMetadataCommand toFileMetadataCommand(com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest request);

    @Mapping(source = "applicantUserId", target = "applicantUserId")
    @Mapping(source = "request.businessLicenseFile", target = "businessLicenseFile")
    OrganizationApplicationCreateCommand toCommand(OrganizationApplicationCreateRequest request, Long applicantUserId);

    FindAllOrganizationApplicationsResponse toResponse(OrganizationApplication application);

    @Mapping(source = "items", target = "applications")
    FindAllOrganizationApplicationsListResponse toListResponse(ApplicationListResult result);
}
