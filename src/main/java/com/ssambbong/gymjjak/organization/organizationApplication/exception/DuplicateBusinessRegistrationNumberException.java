package com.ssambbong.gymjjak.organization.organizationApplication.exception;

public class DuplicateBusinessRegistrationNumberException extends OrganizationApplicationException {

    public DuplicateBusinessRegistrationNumberException() {
        super(OrganizationApplicationErrorCode.DUPLICATE_BUSINESS_REGISTRATION_NUMBER);
    }

}
