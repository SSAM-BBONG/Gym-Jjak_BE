package com.ssambbong.gymjjak.organization.organizationApplication.application.usecase;

import com.ssambbong.gymjjak.organization.organizationApplication.application.command.OrganizationApplicationCreateCommand;

public interface OrganizationApplicationCommandUsecase {

    Long createOrganizationApplication(OrganizationApplicationCreateCommand command);

    void approveOrganizationApplication(Long organizationApplicationId, Long reviewedBy);

    void rejectOrganizationApplication(Long organizationApplicationId, Long reviewedBy, String rejectReason);

    void cancelOrganizationApplication(Long organizationApplicationId, Long applicantId);
}
