package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationSearchResult;

public record OrganizationSearchResponse(
        Long organizationId,
        String businessName,
        String representativeName,
        String roadAddress,
        String detailAddress
) {
    public static OrganizationSearchResponse from(OrganizationSearchResult result) {
        return new OrganizationSearchResponse(
                result.organizationId(),
                result.businessName(),
                result.representativeName(),
                result.roadAddress(),
                result.detailAddress()
        );
    }
}
