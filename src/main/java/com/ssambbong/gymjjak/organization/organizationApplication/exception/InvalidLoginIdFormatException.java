package com.ssambbong.gymjjak.organization.organizationApplication.exception;

public class InvalidLoginIdFormatException extends OrganizationApplicationException {

    public InvalidLoginIdFormatException() {
        super(OrganizationApplicationErrorCode.INVALID_LOGIN_ID_FORMAT);
    }
}
