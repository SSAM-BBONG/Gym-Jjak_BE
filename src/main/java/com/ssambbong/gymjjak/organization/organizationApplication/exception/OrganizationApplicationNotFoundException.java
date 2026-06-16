package com.ssambbong.gymjjak.organization.organizationApplication.exception;

public class OrganizationApplicationNotFoundException extends OrganizationApplicationException {

    public OrganizationApplicationNotFoundException() {
        super(OrganizationApplicationErrorCode.ORGANIZATION_APPLICATION_NOT_FOUND);
    }
}