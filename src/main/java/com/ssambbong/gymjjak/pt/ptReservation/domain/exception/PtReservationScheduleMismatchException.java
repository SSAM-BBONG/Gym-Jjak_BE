package com.ssambbong.gymjjak.pt.ptReservation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;


public class PtReservationScheduleMismatchException extends BadRequestException {

    public PtReservationScheduleMismatchException() {
        super(PtReservationErrorCode.PT_RESERVATION_SCHEDULE_MISMATCH,
                PtReservationErrorCode.PT_RESERVATION_SCHEDULE_MISMATCH.getMessage());
    }
}
