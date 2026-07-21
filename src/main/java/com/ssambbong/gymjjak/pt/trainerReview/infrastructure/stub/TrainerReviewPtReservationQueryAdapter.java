package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.stub;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;

import java.util.List;
import com.ssambbong.gymjjak.pt.trainerReview.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.pt.trainerReview.application.port.ReservationResult;
import com.ssambbong.gymjjak.pt.trainerReview.domain.exception.TrainerReviewPtReservationNotFoundException;
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

        int progressCount = ptReservationRepository.countProgressByUserIdAndPtCourseId(userId, ptCourseId);

        List<PtReservation> sessions =
                ptReservationRepository.findAllByUserId(userId, null).stream()
                        .filter(r -> r.getPtCourseId().equals(ptCourseId))
                        .toList();
        boolean allCancelled = sessions.stream()
                .allMatch(r -> r.getStatus() == PtReservationStatus.CANCELLED);
        boolean allCompleted = sessions.stream()
                .allMatch(r -> r.getStatus() == PtReservationStatus.COMPLETED);

        boolean completed = !allCancelled && (allCompleted || progressCount >= reservation.getTotalSessionCount());

        return new ReservationResult(completed, reservation.getTrainerProfileId());
    }
}
