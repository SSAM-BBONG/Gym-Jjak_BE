package com.ssambbong.gymjjak.pt.ptReservation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class PtReservationForbiddenException extends ForbiddenException {
    public PtReservationForbiddenException() {
        super(PtReservationErrorCode.PT_RESERVATION_FORBIDDEN,
                PtReservationErrorCode.PT_RESERVATION_FORBIDDEN.getMessage());
    }
}
