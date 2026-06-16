package com.ssambbong.gymjjak.organization.organization.exception;

public class OrganizationNotFoundException extends OrganizationException {

    public OrganizationNotFoundException() {
        super(OrganizationErrorCode.ORGANIZATION_NOT_FOUND);
    }
}
