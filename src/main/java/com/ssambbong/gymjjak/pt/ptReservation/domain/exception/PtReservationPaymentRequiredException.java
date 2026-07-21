package com.ssambbong.gymjjak.pt.ptReservation.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class PtReservationPaymentRequiredException extends ForbiddenException {
    public PtReservationPaymentRequiredException() {
        super(PtReservationErrorCode.PT_RESERVATION_PAYMENT_REQUIRED,
                PtReservationErrorCode.PT_RESERVATION_PAYMENT_REQUIRED.getMessage());
    }
}
