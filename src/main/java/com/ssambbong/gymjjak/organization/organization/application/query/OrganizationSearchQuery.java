package com.ssambbong.gymjjak.organization.organization.application.query;

public record OrganizationSearchQuery(
        String keyword,
        int page,
        int size
) {}
