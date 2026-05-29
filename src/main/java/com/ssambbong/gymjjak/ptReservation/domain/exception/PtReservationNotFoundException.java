package com.ssambbong.gymjjak.ptReservation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class PtReservationNotFoundException extends NotFoundException {

    public PtReservationNotFoundException() {
        super(PtReservationErrorCode.PT_RESERVATION_NOT_FOUND,
                PtReservationErrorCode.PT_RESERVATION_NOT_FOUND.getMessage());
    }
}
