package com.ssambbong.gymjjak.pt.ptReservation.application.usecase;

import com.ssambbong.gymjjak.pt.ptReservation.application.result.PtReservationCalendarResult;

import java.util.List;

public interface FindPtReservationCalendarUsecase {
    List<PtReservationCalendarResult> findPtReservationCalendar(
            Long userId,
            int year,
            int month
    );
}
