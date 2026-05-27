package com.ssambbong.gymjjak.organization.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public abstract class OrganizationApplicationException extends ApplicationException {

  private final OrganizationApplicationErrorCode organizationApplicationErrorCode;

  public OrganizationApplicationException(OrganizationApplicationErrorCode organizationApplicationErrorCode) {
    super(organizationApplicationErrorCode, organizationApplicationErrorCode.getMessage());
    this.organizationApplicationErrorCode = organizationApplicationErrorCode;

  }

  protected OrganizationApplicationException(OrganizationApplicationErrorCode organizationApplicationErrorCode, String message) {
    super(organizationApplicationErrorCode, message);
    this.organizationApplicationErrorCode = organizationApplicationErrorCode;
  }

  protected OrganizationApplicationException(OrganizationApplicationErrorCode organizationApplicationErrorCode, String message, Throwable cause) {
    super(organizationApplicationErrorCode, organizationApplicationErrorCode.getMessage(), cause);
    this.organizationApplicationErrorCode = organizationApplicationErrorCode;
  }
}
