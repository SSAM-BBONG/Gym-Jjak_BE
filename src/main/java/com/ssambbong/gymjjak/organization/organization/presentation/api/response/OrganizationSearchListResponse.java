package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationSearchListResult;

import java.util.List;

public record OrganizationSearchListResponse(
        List<OrganizationSearchResponse> content,
        int page,
        int size,
        boolean hasNext
) {
    public static OrganizationSearchListResponse from(OrganizationSearchListResult result) {
        return new OrganizationSearchListResponse(
                result.content().stream().map(OrganizationSearchResponse::from).toList(),
                result.page(),
                result.size(),
                result.hasNext()
        );
    }
}
