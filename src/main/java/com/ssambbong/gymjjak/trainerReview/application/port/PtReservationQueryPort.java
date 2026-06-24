package com.ssambbong.gymjjak.trainerReview.application.port;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

public interface PtReservationQueryPort {

    ReservationInfo findById(Long ptReservationId);

    record ReservationInfo(
            Long userId,
            Long trainerProfileId,
            Long ptCourseId,
            PtReservationStatus status
    ) {}
}
