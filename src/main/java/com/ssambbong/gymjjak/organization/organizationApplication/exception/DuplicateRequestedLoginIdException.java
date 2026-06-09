package com.ssambbong.gymjjak.organization.organizationApplication.exception;

public class DuplicateRequestedLoginIdException extends OrganizationApplicationException {

    public DuplicateRequestedLoginIdException() {
        super(OrganizationApplicationErrorCode.DUPLICATE_REQUESTED_LOGIN_ID);
    }

}
