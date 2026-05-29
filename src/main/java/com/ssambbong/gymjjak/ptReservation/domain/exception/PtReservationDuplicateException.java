package com.ssambbong.gymjjak.ptReservation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class PtReservationDuplicateException extends ConflictException {
    public PtReservationDuplicateException() {
        super(PtReservationErrorCode.PT_RESERVATION_DUPLICATE,
                PtReservationErrorCode.PT_RESERVATION_DUPLICATE.getMessage());
    }
}
