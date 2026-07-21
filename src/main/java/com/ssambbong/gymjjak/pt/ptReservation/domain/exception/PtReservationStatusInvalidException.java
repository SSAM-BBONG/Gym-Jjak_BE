package com.ssambbong.gymjjak.pt.ptReservation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class PtReservationStatusInvalidException extends ConflictException {
    public PtReservationStatusInvalidException() {
        super(PtReservationErrorCode.PT_RESERVATION_STATUS_INVALID);
    }

    public PtReservationStatusInvalidException(PtReservationErrorCode errorCode) {
        super(errorCode);
    }
}
