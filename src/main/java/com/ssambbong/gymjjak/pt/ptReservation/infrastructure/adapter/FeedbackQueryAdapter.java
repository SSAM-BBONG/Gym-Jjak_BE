package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence.FeedbackJpaEntity;
import com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence.SpringDataFeedbackRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.FeedbackQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedbackQueryAdapter implements FeedbackQueryPort {

    private final SpringDataFeedbackRepository feedbackRepository;

    @Override
    public Map<Long, Long> findFeedbackIdMapByReservationIds(List<Long> ptReservationIds) {
        if (ptReservationIds.isEmpty()) return Map.of();
        List<FeedbackJpaEntity> feedbacks =
                feedbackRepository.findAllByPtReservationIdInAndDeletedAtIsNull(ptReservationIds);

        return feedbacks.stream()
                .collect(Collectors.toMap(
                        FeedbackJpaEntity::getPtCurriculumId,
                        FeedbackJpaEntity::getId,
                        (a, b) -> a
                ));
    }
}
