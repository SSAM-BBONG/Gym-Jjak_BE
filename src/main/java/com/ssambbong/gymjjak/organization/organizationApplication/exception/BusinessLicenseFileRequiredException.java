package com.ssambbong.gymjjak.organization.organizationApplication.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class BusinessLicenseFileRequiredException extends BadRequestException {

    public BusinessLicenseFileRequiredException() {
        super(OrganizationApplicationErrorCode.BUSINESS_LICENSE_FILE_REQUIRED);
    }
}
