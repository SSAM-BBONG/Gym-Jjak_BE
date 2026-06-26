package com.ssambbong.gymjjak.trainerReview.infrastructure.stub;

import com.ssambbong.gymjjak.trainerReview.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.trainerReview.application.port.ReservationResult;
import org.springframework.stereotype.Component;

@Component
public class TrainerReviewPtReservationQueryAdapter implements PtReservationQueryPort {

    @Override
    public ReservationResult findReservation(Long ptReservationId, Long userId, Long ptCourseId) {
        return null;
    }
}
