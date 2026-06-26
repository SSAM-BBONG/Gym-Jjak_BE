package com.ssambbong.gymjjak.pt.feedback.infrastructure.actuator;

import com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence.SpringDataFeedbackRepository;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Endpoint(id = "feedback")
@RequiredArgsConstructor
public class FeedbackMetricEndpoint {

    private final SpringDataFeedbackRepository feedbackRepository;
    private final SpringDataPtReservationRepository ptReservationRepository;

    @ReadOperation
    public FeedbackSummary summary() {
        long totalFeedbacks = feedbackRepository.countByDeletedAtIsNull();
        long reservationsWithFeedback = feedbackRepository.countDistinctReservationsWithFeedback();
        long totalReservations = ptReservationRepository.count();
        double feedbackWriteRate = totalReservations == 0 ? 0.0
                : Math.round((double) reservationsWithFeedback / totalReservations * 1000) / 10.0;

        List<TrainerFeedbackTimeItem> avgWriteTimeByTrainer =
                feedbackRepository.findAvgFeedbackHoursByTrainer().stream()
                        .map(row -> new TrainerFeedbackTimeItem(
                                ((Number) row[0]).longValue(),
                                row[1] != null
                                        ? Math.round(((Number) row[1]).doubleValue() * 10) / 10.0
                                        : 0.0
                        ))
                        .toList();

        return new FeedbackSummary(totalFeedbacks, reservationsWithFeedback,
                totalReservations, feedbackWriteRate, avgWriteTimeByTrainer);
    }

    public record FeedbackSummary(
            long totalFeedbackCount,
            long reservationsWithFeedback,
            long totalReservationCount,
            double feedbackWriteRate, // 피드백 작성률 (%)
            List<TrainerFeedbackTimeItem> avgWriteTimeByTrainer  // 트레이너별 평균 소요 시간
    ) {}

    public record TrainerFeedbackTimeItem(
            Long trainerProfileId,
            double avgHours  // 예약 시작 후 첫 피드백 작성까지 평균 시간
    ) {}
}
