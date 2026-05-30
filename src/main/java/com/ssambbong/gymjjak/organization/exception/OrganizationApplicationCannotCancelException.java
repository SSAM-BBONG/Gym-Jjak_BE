package com.ssambbong.gymjjak.organization.exception;

public class OrganizationApplicationCannotCancelException extends OrganizationApplicationException {

    public OrganizationApplicationCannotCancelException(OrganizationApplicationErrorCode errorCode) {
        super(errorCode);
    }
}
