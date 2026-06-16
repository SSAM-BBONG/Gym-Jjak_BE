package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptReservation.application.port.FeedbackQueryPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedbackQueryAdapter implements FeedbackQueryPort {

    private final EntityManager em;

    @Override
    public LocalDate findLastFeedbackDate(Long ptReservationId) {
        // TODO: feedback 도메인 구현 후 MAX(feedbacks.created_at) 조회로 교체 (현재는 항상 null)
        return null;
    }

    // TODO: feedback 도메인 구현 후 FeedbackRepository(또는 동등한 포트)의
    //       findAllByPtReservationId() 등으로 교체하고, 이 native query/EntityManager 의존성 제거
    @Override
    public Map<Long, Long> findFeedbackIdMapByReservationId(Long ptReservationId) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT pt_curriculum_id, feedback_id
            FROM feedbacks
            WHERE pt_reservation_id = ?1
              AND deleted_at IS NULL
        """)
                .setParameter(1, ptReservationId)
                .getResultList();

        return rows.stream().collect(Collectors.toMap(
                r -> ((Number) r[0]).longValue(),
                r -> ((Number) r[1]).longValue()
        ));
    }

}
