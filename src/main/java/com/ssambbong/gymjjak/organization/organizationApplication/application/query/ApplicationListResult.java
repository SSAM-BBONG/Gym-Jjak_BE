package com.ssambbong.gymjjak.organization.organizationApplication.application.query;

import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;

import java.util.List;

public record ApplicationListResult(
        List<OrganizationApplication> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
