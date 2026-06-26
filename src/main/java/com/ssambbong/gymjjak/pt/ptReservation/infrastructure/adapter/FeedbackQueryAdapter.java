package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence.FeedbackJpaEntity;
import com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence.SpringDataFeedbackRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.FeedbackQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedbackQueryAdapter implements FeedbackQueryPort {

    private final SpringDataFeedbackRepository feedbackRepository;

    @Override
    public LocalDate findLastFeedbackDate(Long ptReservationId) {
        return feedbackRepository.findMaxCreatedAtByPtReservationId(ptReservationId)
                .map(dt -> dt.toLocalDate())
                .orElse(null);
    }

    @Override
    public Map<Long, Long> findFeedbackIdMapByReservationId(Long ptReservationId) {
        List<FeedbackJpaEntity> feedbacks =
                feedbackRepository.findAllByPtReservationIdAndDeletedAtIsNull(ptReservationId);

        return feedbacks.stream()
                .collect(Collectors.toMap(
                        FeedbackJpaEntity::getPtCurriculumId,
                        FeedbackJpaEntity::getId
                ));
    }
}
