package com.ssambbong.gymjjak.trainerReview.infrastructure.metrics;

import com.ssambbong.gymjjak.trainerReview.application.event.TrainerReviewCreatedEvent;
import com.ssambbong.gymjjak.trainerReview.application.event.TrainerReviewDeletedEvent;
import com.ssambbong.gymjjak.trainerReview.application.event.TrainerReviewUpdatedEvent;
import com.ssambbong.gymjjak.trainerReview.application.port.TrainerReviewMetricsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerReviewMetricsEventListener {

    private final TrainerReviewMetricsPort trainerReviewMetricsPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(TrainerReviewCreatedEvent event) {
        try {
            trainerReviewMetricsPort.recordCreated(event.rating());
        } catch (Exception e) {
            log.warn("event=metrics_record_failed metric=trainer_review_created", e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdated(TrainerReviewUpdatedEvent event) {
        try {
            trainerReviewMetricsPort.recordUpdated();
        } catch (Exception e) {
            log.warn("event=metrics_record_failed metric=trainer_review_updated", e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleted(TrainerReviewDeletedEvent event) {
        try {
            trainerReviewMetricsPort.recordDeleted();
        } catch (Exception e) {
            log.warn("event=metrics_record_failed metric=trainer_review_deleted", e);
        }
    }
}
