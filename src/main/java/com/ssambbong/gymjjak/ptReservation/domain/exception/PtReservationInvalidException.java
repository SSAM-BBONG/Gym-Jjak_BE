package com.ssambbong.gymjjak.ptReservation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class PtReservationInvalidException extends BadRequestException {

    public PtReservationInvalidException() {
        super(PtReservationErrorCode.PT_RESERVATION_INVALID,
                PtReservationErrorCode.PT_RESERVATION_INVALID.getMessage());
    }
}
