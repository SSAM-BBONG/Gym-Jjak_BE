package com.ssambbong.gymjjak.trainerReview.infrastructure.metrics;

import com.ssambbong.gymjjak.trainerReview.application.port.TrainerReviewMetricsPort;
import com.ssambbong.gymjjak.trainerReview.domain.repository.TrainerReviewRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainerReviewMetrics implements TrainerReviewMetricsPort {

    private final MeterRegistry meterRegistry;

    public TrainerReviewMetrics(MeterRegistry meterRegistry, TrainerReviewRepository trainerReviewRepository) {
        this.meterRegistry = meterRegistry;

        Gauge.builder("gymjjak.trainer_review.active.total", trainerReviewRepository, TrainerReviewRepository::countActive)
                .description("전체 활성 강사평 수")
                .register(meterRegistry);

        Gauge.builder("gymjjak.trainer_review.average_rating", trainerReviewRepository, TrainerReviewRepository::findAverageRating)
                .description("서비스 전체 평균 평점")
                .register(meterRegistry);
    }

    @Override
    public void recordCreated(int rating) {
        Counter.builder("gymjjak.trainer_review.created")
                .description("강사평 작성 횟수")
                .tag("rating", String.valueOf(rating))
                .register(meterRegistry)
                .increment();
    }

    @Override
    public void recordUpdated() {
        Counter.builder("gymjjak.trainer_review.updated")
                .description("강사평 수정 횟수")
                .register(meterRegistry)
                .increment();
    }

    @Override
    public void recordDeleted() {
        Counter.builder("gymjjak.trainer_review.deleted")
                .description("강사평 삭제 횟수")
                .register(meterRegistry)
                .increment();
    }
}
