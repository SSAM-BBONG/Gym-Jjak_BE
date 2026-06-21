package com.ssambbong.gymjjak.pt.ptReservation.application.usecase;

import com.ssambbong.gymjjak.pt.ptReservation.application.command.ChangePtReservationStatusCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CreatePtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response.ChangePtReservationStatusResponse;

public interface PtReservationCommandUseCase {

    // PT 예약 생성 → 생성된 reservationId 반환
    Long createPtReservation(CreatePtReservationCommand command);

    ChangePtReservationStatusResponse changePtReservationStatus(ChangePtReservationStatusCommand command);
}
