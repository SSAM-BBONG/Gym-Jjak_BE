package com.ssambbong.gymjjak.pt.ptReservation.application.usecase;

import com.ssambbong.gymjjak.pt.ptReservation.application.command.CreatePtReservationCommand;

public interface PtReservationCommandUseCase {

    // PT 예약 생성 → 생성된 reservationId 반환
    Long createPtReservation(CreatePtReservationCommand command);
}
