package com.ssambbong.gymjjak.organization.organization.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;

public abstract class OrganizationException extends ApplicationException {

    public OrganizationException(OrganizationErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}
