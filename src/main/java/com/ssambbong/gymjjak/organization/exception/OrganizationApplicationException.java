package com.ssambbong.gymjjak.organization.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;

public abstract class OrganizationApplicationException extends ApplicationException {

  public OrganizationApplicationException(OrganizationApplicationErrorCode errorCode) {
    super(errorCode, errorCode.getMessage());
  }

  protected OrganizationApplicationException(OrganizationApplicationErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  protected OrganizationApplicationException(OrganizationApplicationErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
