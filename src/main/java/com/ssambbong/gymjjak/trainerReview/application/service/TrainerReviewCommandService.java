package com.ssambbong.gymjjak.trainerReview.application.service;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.trainerReview.application.command.CreateTrainerReviewCommand;
import com.ssambbong.gymjjak.trainerReview.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.trainerReview.application.usecase.TrainerReviewCommandUseCase;
import com.ssambbong.gymjjak.trainerReview.domain.exception.PtReservationNotCompletedException;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewAlreadyExistsException;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewForbiddenException;
import com.ssambbong.gymjjak.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.trainerReview.domain.repository.TrainerReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerReviewCommandService implements TrainerReviewCommandUseCase {

    private final TrainerReviewRepository trainerReviewRepository;
    private final PtReservationQueryPort ptReservationQueryPort;

    @Override
    @Transactional
    public Long createReview(CreateTrainerReviewCommand command) {
        PtReservationQueryPort.ReservationInfo reservation =
                ptReservationQueryPort.findById(command.ptReservationId());

        if (!reservation.userId().equals(command.userId())) {
            throw new TrainerReviewForbiddenException();
        }

        if (reservation.status() != PtReservationStatus.COMPLETED) {
            throw new PtReservationNotCompletedException();
        }

        if (trainerReviewRepository.existsByPtReservationId(command.ptReservationId())) {
            throw new TrainerReviewAlreadyExistsException();
        }

        TrainerReview trainerReview = TrainerReview.create(
                command.userId(),
                reservation.trainerProfileId(),
                reservation.ptCourseId(),
                command.ptReservationId(),
                command.rating(),
                command.content()
        );

        try {
            return trainerReviewRepository.save(trainerReview);
        } catch (DataIntegrityViolationException e) {
            if (isReservationUniqueConstraint(e)) {
                throw new TrainerReviewAlreadyExistsException(e);
            }
            throw e;
        }
    }

    private boolean isReservationUniqueConstraint(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();
        return message != null && message.contains("uk_trainer_reviews_reservation");
    }
}
