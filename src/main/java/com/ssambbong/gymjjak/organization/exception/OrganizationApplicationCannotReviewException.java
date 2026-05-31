package com.ssambbong.gymjjak.organization.exception;

public class OrganizationApplicationCannotReviewException extends OrganizationApplicationException {

    public OrganizationApplicationCannotReviewException(OrganizationApplicationErrorCode errorCode) {
        super(errorCode);
    }
}
