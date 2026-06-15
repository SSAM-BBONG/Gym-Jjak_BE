package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptReservation.application.port.FeedbackQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class FeedbackQueryAdapter implements FeedbackQueryPort {

    @Override
    public LocalDate findLastFeedbackDate(Long ptReservationId) {
        // TODO: feedback 도메인 구현 후 MAX(feedbacks.created_at) 조회로 교체 (현재는 항상 null)
        return null;
    }
}
