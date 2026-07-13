package com.ssambbong.gymjjak.inbody.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class InbodyRequiredFieldException extends BadRequestException {

    public InbodyRequiredFieldException(InbodyErrorCode errorCode) {
        super(errorCode);
    }
}
