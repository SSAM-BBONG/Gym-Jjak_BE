package com.ssambbong.gymjjak.organization.organization.application.query;

import java.util.List;

public record OrganizationListResult(
        List<OrganizationAdminView> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
