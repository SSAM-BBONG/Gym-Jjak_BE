package com.ssambbong.gymjjak.organization.organizationApplication.exception;

public class OrganizationApplicationCannotReviewException extends OrganizationApplicationException {

    public OrganizationApplicationCannotReviewException(OrganizationApplicationErrorCode errorCode) {
        super(errorCode);
    }
}
