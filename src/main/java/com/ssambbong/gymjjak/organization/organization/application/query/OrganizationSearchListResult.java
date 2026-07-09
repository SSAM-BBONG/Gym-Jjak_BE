package com.ssambbong.gymjjak.organization.organization.application.query;

import java.util.List;

public record OrganizationSearchListResult(
        List<OrganizationSearchResult> content,
        int page,
        int size,
        boolean hasNext
) {}
