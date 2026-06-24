package com.ssambbong.gymjjak.calendar.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

public class CalendarException extends ApplicationException {
    public CalendarException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}
