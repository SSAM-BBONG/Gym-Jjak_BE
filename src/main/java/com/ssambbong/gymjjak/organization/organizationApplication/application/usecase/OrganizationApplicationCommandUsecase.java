package com.ssambbong.gymjjak.organization.organizationApplication.application.usecase;

import com.ssambbong.gymjjak.organization.organizationApplication.application.command.OrganizationApplicationCreateCommand;
import org.springframework.web.multipart.MultipartFile;

public interface OrganizationApplicationCommandUsecase {

    Long createOrganizationApplication(MultipartFile businessLicenseFile, OrganizationApplicationCreateCommand command);

    void approveOrganizationApplication(Long organizationApplicationId, Long reviewedBy);

    void rejectOrganizationApplication(Long organizationApplicationId, Long reviewedBy, String rejectReason);

    void cancelOrganizationApplication(Long organizationApplicationId, Long applicantId);
}
