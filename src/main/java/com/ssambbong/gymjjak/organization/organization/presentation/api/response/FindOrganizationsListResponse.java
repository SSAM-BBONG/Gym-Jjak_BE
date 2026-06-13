package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListResult;

import java.util.List;

public record FindOrganizationsListResponse(
        List<FindOrganizationsResponse> organizations,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static FindOrganizationsListResponse from(OrganizationListResult result) {
        return new FindOrganizationsListResponse(
                result.items().stream()
                        .map(FindOrganizationsResponse::from)
                        .toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
