package com.ssambbong.gymjjak.organization.organization.application.query;

public record OrganizationSearchResult(
        Long organizationId,
        String businessName,
        String representativeName,
        String roadAddress,
        String detailAddress,
        String facilityPhone
) {}
