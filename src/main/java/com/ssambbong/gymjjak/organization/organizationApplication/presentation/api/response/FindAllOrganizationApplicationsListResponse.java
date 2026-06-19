package com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.response;

import java.util.List;

public record FindAllOrganizationApplicationsListResponse(
        List<FindAllOrganizationApplicationsResponse> applications,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
