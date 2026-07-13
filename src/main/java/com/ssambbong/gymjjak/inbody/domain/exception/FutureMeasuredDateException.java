package com.ssambbong.gymjjak.inbody.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

import java.time.LocalDate;

public class FutureMeasuredDateException extends BadRequestException {

    public FutureMeasuredDateException(LocalDate measuredDate) {
        super(InbodyErrorCode.MEASURED_DATE_IN_FUTURE);
        addContext("measuredDate", measuredDate);
    }
}
