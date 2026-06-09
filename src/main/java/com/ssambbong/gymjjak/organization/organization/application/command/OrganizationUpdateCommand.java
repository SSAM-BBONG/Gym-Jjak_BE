package com.ssambbong.gymjjak.organization.organization.application.command;

public record OrganizationUpdateCommand(
        Long organizationAccountId,
        String facilityPhone,
        String instagramUrl,
        String blogUrl,
        String websiteUrl
) {
}
