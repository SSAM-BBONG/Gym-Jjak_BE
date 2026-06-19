package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import java.util.List;

public record FindOrganizationsListResponse(
        List<FindOrganizationsResponse> organizations,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
