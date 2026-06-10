package com.ssambbong.gymjjak.global.domain.auth;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

public class AuthException extends ApplicationException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

}