package com.ssambbong.gymjjak.organization.organization.application.usecase;

public interface OrganizationCommandUseCase {

    void updateOrganization(Long organizationAccountId, String facilityPhone, String instagramUrl, String blogUrl, String websiteUrl);
}
