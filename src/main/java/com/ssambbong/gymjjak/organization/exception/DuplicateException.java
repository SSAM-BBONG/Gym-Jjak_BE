package com.ssambbong.gymjjak.organization.exception;

public class DuplicateException extends OrganizationApplicationException {


    public DuplicateException() {
        super(OrganizationApplicationErrorCode.DUPLICATE_BUSINESS_REGISTRATION_NUMBER);
    }

}
