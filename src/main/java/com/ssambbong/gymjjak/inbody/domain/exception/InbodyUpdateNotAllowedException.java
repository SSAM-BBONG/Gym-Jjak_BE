package com.ssambbong.gymjjak.inbody.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class InbodyUpdateNotAllowedException extends BadRequestException {
    public InbodyUpdateNotAllowedException(Long inbodyId) {
        super(InbodyErrorCode.UPDATE_NOT_ALLOWED);
        addContext("inbodyId", inbodyId);
    }
}
