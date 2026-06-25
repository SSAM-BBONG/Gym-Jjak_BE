package com.ssambbong.gymjjak.trainerReview.application.service;

import com.ssambbong.gymjjak.trainerReview.application.command.CreateTrainerReviewCommand;
import com.ssambbong.gymjjak.trainerReview.application.command.DeleteTrainerReviewCommand;
import com.ssambbong.gymjjak.trainerReview.application.command.UpdateTrainerReviewCommand;
import com.ssambbong.gymjjak.trainerReview.application.event.TrainerReviewCreatedEvent;
import com.ssambbong.gymjjak.trainerReview.application.event.TrainerReviewDeletedEvent;
import com.ssambbong.gymjjak.trainerReview.application.event.TrainerReviewUpdatedEvent;
import com.ssambbong.gymjjak.trainerReview.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.trainerReview.application.port.ReservationResult;
import com.ssambbong.gymjjak.trainerReview.application.usecase.TrainerReviewCommandUseCase;
import org.springframework.context.ApplicationEventPublisher;
import com.ssambbong.gymjjak.trainerReview.domain.exception.PtReservationNotCompletedException;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewAlreadyExistsException;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewForbiddenException;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewNotFoundException;
import com.ssambbong.gymjjak.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.trainerReview.domain.repository.TrainerReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrainerReviewCommandService implements TrainerReviewCommandUseCase {

    private final TrainerReviewRepository trainerReviewRepository;
    private final PtReservationQueryPort ptReservationQueryPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Long createReview(CreateTrainerReviewCommand command) {
        ReservationResult reservation =
                ptReservationQueryPort.findReservation(command.ptReservationId(), command.userId(), command.ptCourseId());

        if (!reservation.completed()) {
            throw new PtReservationNotCompletedException();
        }

        if (trainerReviewRepository.existsByPtReservationId(command.ptReservationId())) {
            throw new TrainerReviewAlreadyExistsException();
        }

        TrainerReview trainerReview = TrainerReview.create(
                command.userId(),
                reservation.trainerProfileId(),
                command.ptCourseId(),
                command.ptReservationId(),
                command.rating(),
                command.content()
        );

        Long reviewId = trainerReviewRepository.save(trainerReview);
        eventPublisher.publishEvent(new TrainerReviewCreatedEvent(command.rating()));
        return reviewId;
    }

    @Override
    @Transactional
    public Long updateReview(UpdateTrainerReviewCommand command) {
        TrainerReview review = trainerReviewRepository.findActiveById(command.reviewId())
                .orElseThrow(TrainerReviewNotFoundException::new);

        if (!review.getUserId().equals(command.userId())) {
            throw new TrainerReviewForbiddenException();
        }

        Long reviewId = trainerReviewRepository.save(review.update(command.rating(), command.content()));
        eventPublisher.publishEvent(new TrainerReviewUpdatedEvent());
        return reviewId;
    }

    @Override
    @Transactional
    public void deleteReview(DeleteTrainerReviewCommand command) {
        TrainerReview review = trainerReviewRepository.findActiveById(command.reviewId())
                .orElseThrow(TrainerReviewNotFoundException::new);

        if (!review.getUserId().equals(command.userId())) {
            throw new TrainerReviewForbiddenException();
        }

        trainerReviewRepository.save(review.delete());
        eventPublisher.publishEvent(new TrainerReviewDeletedEvent());
    }
}
