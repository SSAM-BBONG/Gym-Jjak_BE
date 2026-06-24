package com.ssambbong.gymjjak.trainerReview.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import com.ssambbong.gymjjak.trainerReview.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewPtReservationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PtReservationQueryAdapter implements PtReservationQueryPort {

    private final PtReservationRepository ptReservationRepository;

    @Override
    public ReservationInfo findById(Long ptReservationId) {
        return ptReservationRepository.findById(ptReservationId)
                .map(r -> new ReservationInfo(
                        r.getUserId(),
                        r.getTrainerProfileId(),
                        r.getPtCourseId(),
                        r.getStatus()
                ))
                .orElseThrow(TrainerReviewPtReservationNotFoundException::new);
    }
}
