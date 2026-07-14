package com.ssambbong.gymjjak.pt.ptReservation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class PtReservationLimitExceededException extends ConflictException {
    public PtReservationLimitExceededException() {
        super(PtReservationErrorCode.PT_RESERVATION_LIMIT_EXCEEDED,
                PtReservationErrorCode.PT_RESERVATION_LIMIT_EXCEEDED.getMessage());
    }
}
