package com.ssambbong.gymjjak.organization.application.usecase;

import com.ssambbong.gymjjak.organization.application.command.OrganizationApplicationCreateCommand;

public interface OrganizationApplicationUsecase {

    Long createOrganizationApplication(OrganizationApplicationCreateCommand command);
}
