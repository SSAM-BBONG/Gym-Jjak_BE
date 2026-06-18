package com.ssambbong.gymjjak.user.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

public class MailSendException extends ApplicationException {
    public MailSendException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}
