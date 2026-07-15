package com.ssambbong.gymjjak.inbody.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class InbodyNotFoundException extends NotFoundException {
    public InbodyNotFoundException(Long inbodyId) {
        super(InbodyErrorCode.INBODY_NOT_FOUND);
        addContext("inbodyId", inbodyId);
    }
}
