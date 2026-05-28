package com.ssambbong.gymjjak.user.application.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

public class UserException extends ApplicationException {

    public UserException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}
