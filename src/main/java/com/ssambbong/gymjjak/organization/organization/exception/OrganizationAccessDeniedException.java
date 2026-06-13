package com.ssambbong.gymjjak.organization.organization.exception;

public class OrganizationAccessDeniedException extends OrganizationException {

    public OrganizationAccessDeniedException() {
        super(OrganizationErrorCode.ORGANIZATION_ACCESS_DENIED);
    }
}
