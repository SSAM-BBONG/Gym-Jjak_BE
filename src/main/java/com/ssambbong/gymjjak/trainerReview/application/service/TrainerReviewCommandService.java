package com.ssambbong.gymjjak.trainerReview.application.service;

import com.ssambbong.gymjjak.trainerReview.application.command.CreateTrainerReviewCommand;
import com.ssambbong.gymjjak.trainerReview.application.command.DeleteTrainerReviewCommand;
import com.ssambbong.gymjjak.trainerReview.application.command.UpdateTrainerReviewCommand;
import com.ssambbong.gymjjak.trainerReview.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.trainerReview.application.port.ReservationResult;
import com.ssambbong.gymjjak.trainerReview.application.port.TrainerReviewMetricsPort;
import com.ssambbong.gymjjak.trainerReview.application.usecase.TrainerReviewCommandUseCase;
import com.ssambbong.gymjjak.trainerReview.domain.exception.PtReservationNotCompletedException;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewAlreadyExistsException;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewForbiddenException;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewNotFoundException;
import com.ssambbong.gymjjak.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.trainerReview.domain.repository.TrainerReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerReviewCommandService implements TrainerReviewCommandUseCase {

    private final TrainerReviewRepository trainerReviewRepository;
    private final PtReservationQueryPort ptReservationQueryPort;
    private final TrainerReviewMetricsPort trainerReviewMetricsPort;

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
        recordMetricSafely(() -> trainerReviewMetricsPort.recordCreated(command.rating()), "recordCreated");
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
        recordMetricSafely(trainerReviewMetricsPort::recordUpdated, "recordUpdated");
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
        recordMetricSafely(trainerReviewMetricsPort::recordDeleted, "recordDeleted");
    }

    private void recordMetricSafely(Runnable metricCall, String metricName) {
        Runnable safeCall = () -> {
            try {
                metricCall.run();
            } catch (Exception e) {
                log.warn("메트릭 기록 실패 - metric: {}", metricName, e);
            }
        };
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    safeCall.run();
                }
            });
        } else {
            safeCall.run();
        }
    }
}
