package com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.response;

import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListResult;

import java.util.List;

public record FindAllOrganizationApplicationsListResponse(
        List<FindAllOrganizationApplicationsResponse> applications,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static FindAllOrganizationApplicationsListResponse from(ApplicationListResult result) {
        return new FindAllOrganizationApplicationsListResponse(
                result.items().stream()
                        .map(FindAllOrganizationApplicationsResponse::from)
                        .toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
