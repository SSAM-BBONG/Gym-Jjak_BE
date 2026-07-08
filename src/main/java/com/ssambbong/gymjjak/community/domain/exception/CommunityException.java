package com.ssambbong.gymjjak.community.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

public class CommunityException extends ApplicationException {
  public CommunityException(ErrorCode errorCode) {
    super(errorCode, errorCode.getMessage());
  }
}
