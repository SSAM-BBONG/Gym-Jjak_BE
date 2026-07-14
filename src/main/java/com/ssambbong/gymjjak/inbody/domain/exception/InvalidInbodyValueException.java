package com.ssambbong.gymjjak.inbody.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class InvalidInbodyValueException extends BadRequestException {

    public InvalidInbodyValueException(String fieldName) {
        super(InbodyErrorCode.INVALID_INBODY_VALUE);
        addContext("fieldName", fieldName);
    }
}
