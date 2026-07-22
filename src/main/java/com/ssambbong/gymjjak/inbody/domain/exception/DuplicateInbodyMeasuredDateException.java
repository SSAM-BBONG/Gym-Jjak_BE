package com.ssambbong.gymjjak.inbody.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

import java.time.LocalDate;

public class DuplicateInbodyMeasuredDateException extends ConflictException {

    public DuplicateInbodyMeasuredDateException(Long userId, LocalDate measuredDate) {
        super(InbodyErrorCode.DUPLICATE_MEASURED_DATE);
        addContext("userId", userId);
        addContext("measuredDate", measuredDate);
    }
}
