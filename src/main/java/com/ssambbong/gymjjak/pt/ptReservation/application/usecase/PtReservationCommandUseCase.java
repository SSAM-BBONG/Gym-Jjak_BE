package com.ssambbong.gymjjak.pt.ptReservation.application.usecase;

import com.ssambbong.gymjjak.pt.ptReservation.application.command.CancelPtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.ChangePtReservationStatusCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CreatePtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;

public interface PtReservationCommandUseCase {

    // PT 예약 생성 → 생성된 reservationId 반환
    Long createPtReservation(CreatePtReservationCommand command);

    // PT 예약 상태 변경 → 변경된 예약 도메인 객체 반환
    PtReservation changePtReservationStatus(ChangePtReservationStatusCommand command);

    // PT 코스 전체 취소
    void cancelPtReservation(CancelPtReservationCommand command);
}
