package com.ssambbong.gymjjak.trainerReview.infrastructure.stub;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import com.ssambbong.gymjjak.trainerReview.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.trainerReview.application.port.ReservationResult;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewPtReservationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerReviewPtReservationQueryAdapter implements PtReservationQueryPort {

    private final PtReservationRepository ptReservationRepository;

    @Override
    public ReservationResult findReservation(Long ptReservationId, Long userId, Long ptCourseId) {
        PtReservation reservation = ptReservationRepository.findById(ptReservationId)
                .orElseThrow(TrainerReviewPtReservationNotFoundException::new);

        if (!reservation.getUserId().equals(userId) || !reservation.getPtCourseId().equals(ptCourseId)) {
            throw new TrainerReviewPtReservationNotFoundException();
        }

        return new ReservationResult(
                reservation.getStatus() == PtReservationStatus.COMPLETED,
                reservation.getTrainerProfileId()
        );
    }
}
