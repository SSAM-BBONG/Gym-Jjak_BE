package com.ssambbong.gymjjak.organization.organization.application.usecase;

import com.ssambbong.gymjjak.organization.organization.application.command.OrganizationUpdateCommand;

public interface OrganizationCommandUseCase {

    void updateOrganization(OrganizationUpdateCommand command);
}
