package com.ssambbong.gymjjak.organization.application.usecase;

import com.ssambbong.gymjjak.organization.application.command.OrganizationApplicationCreateCommand;
import org.springframework.web.multipart.MultipartFile;

public interface OrganizationApplicationCommandUsecase {

    Long createOrganizationApplication(MultipartFile businessLicenseFile, OrganizationApplicationCreateCommand command);

    void approveOrganizationApplication(Long organizationApplicationId, Long reviewedBy);
}
