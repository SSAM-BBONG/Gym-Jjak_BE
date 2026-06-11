package com.ssambbong.gymjjak.organization.organization.application.query;

import com.ssambbong.gymjjak.organization.organization.application.OrganizationAdminView;

import java.util.List;

public record OrganizationListResult(
        List<OrganizationAdminView> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
