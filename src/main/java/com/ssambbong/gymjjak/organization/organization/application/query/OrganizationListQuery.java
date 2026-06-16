package com.ssambbong.gymjjak.organization.organization.application.query;

public record OrganizationListQuery(
        int page,
        int size,
        String keyword) {
}
