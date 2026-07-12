package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.application.port.PtCoursePaymentQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PtCoursePaymentQueryAdapter implements PtCoursePaymentQueryPort {

    private final EntityManager em;

    // pt_courses에서 결제에 필요한 코스명·가격 조회 (payment 도메인이 ptCourse JPA Entity에 직접 의존하지 않도록 분리)
    @Override
    public PtCoursePaymentInfo findPtCoursePaymentInfo(Long ptCourseId) {
        List<?> results = em.createNativeQuery("""
                SELECT pc.title, pc.price
                FROM pt_courses pc
                WHERE pc.pt_course_id = ?1
                  AND pc.deleted_at IS NULL
                """)
                .setParameter(1, ptCourseId)
                .getResultList();

        Object[] result = (Object[]) results.stream()
                .findFirst()
                .orElseThrow(PtCourseNotFoundException::new);

        return new PtCoursePaymentInfo(
                (String) result[0],
                ((Number) result[1]).intValue()
        );
    }
}
