package com.ssambbong.gymjjak.organization.organizationApplication.exception;

public class OrganizationApplicationAccessDeniedException extends OrganizationApplicationException {

    public OrganizationApplicationAccessDeniedException() {
        super(OrganizationApplicationErrorCode.ORGANIZATION_APPLICATION_ACCESS_DENIED);
    }
}
