package com.ssambbong.gymjjak.trainerReview.application.port;

public interface PtReservationQueryPort {

    ReservationResult findReservation(Long ptReservationId, Long userId, Long ptCourseId);
}
