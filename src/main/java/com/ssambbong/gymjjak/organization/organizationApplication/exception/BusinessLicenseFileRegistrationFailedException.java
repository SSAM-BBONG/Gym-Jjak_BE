package com.ssambbong.gymjjak.organization.organizationApplication.exception;

public class BusinessLicenseFileRegistrationFailedException extends OrganizationApplicationException {

    public BusinessLicenseFileRegistrationFailedException() {
        super(OrganizationApplicationErrorCode.BUSINESS_LICENSE_FILE_REGISTRATION_FAILED);
    }
}
