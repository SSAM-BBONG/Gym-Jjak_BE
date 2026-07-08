package com.ssambbong.gymjjak.pt.trainerReview.application.port;

public interface PtReservationQueryPort {

    ReservationResult findReservation(Long ptReservationId, Long userId, Long ptCourseId);
}
