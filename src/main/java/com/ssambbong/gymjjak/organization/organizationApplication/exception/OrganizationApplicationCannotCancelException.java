package com.ssambbong.gymjjak.organization.organizationApplication.exception;

public class OrganizationApplicationCannotCancelException extends OrganizationApplicationException {

    public OrganizationApplicationCannotCancelException(OrganizationApplicationErrorCode errorCode) {
        super(errorCode);
    }
}
