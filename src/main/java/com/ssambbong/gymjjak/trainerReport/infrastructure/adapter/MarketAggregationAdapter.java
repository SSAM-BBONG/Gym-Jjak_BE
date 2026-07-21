package com.ssambbong.gymjjak.trainerReport.infrastructure.adapter;

import com.ssambbong.gymjjak.trainerReport.application.port.MarketAggregationPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MarketAggregationAdapter implements MarketAggregationPort {

    private final EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<CourseEnrollmentStat> findMonthlyEnrollmentStats(LocalDateTime monthStart, LocalDateTime monthEndExclusive) {
        // 취소된 예약은 "실제 등록"으로 보지 않으므로 제외한다. 삭제된 상품도 제외한다.
        List<Object[]> rows = em.createNativeQuery("""
                SELECT pc.part, pc.price, pc.total_session_count, COUNT(*) AS enrollment_count
                FROM pt_reservations pr
                JOIN pt_courses pc ON pr.pt_course_id = pc.pt_course_id
                WHERE pr.created_at >= ?1 AND pr.created_at < ?2
                  AND pr.cancelled_at IS NULL
                  AND pc.deleted_at IS NULL
                GROUP BY pc.part, pc.price, pc.total_session_count
                """)
                .setParameter(1, monthStart)
                .setParameter(2, monthEndExclusive)
                .getResultList();

        return rows.stream()
                .map(row -> new CourseEnrollmentStat(
                        (String) row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue(),
                        ((Number) row[3]).longValue()))
                .toList();
    }
}
