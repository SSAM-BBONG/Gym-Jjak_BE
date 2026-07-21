package com.ssambbong.gymjjak.trainerReport.infrastructure.adapter;

import com.ssambbong.gymjjak.trainerReport.application.port.MyPtCourseQueryPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MyPtCourseQueryAdapter implements MyPtCourseQueryPort {

    private final EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<MyPtCourseInfo> findVisibleCoursesByTrainerProfileId(Long trainerProfileId) {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT title, price, total_session_count, part
                FROM pt_courses
                WHERE trainer_profile_id = ?1
                  AND status = 'VISIBLE'
                  AND deleted_at IS NULL
                """)
                .setParameter(1, trainerProfileId)
                .getResultList();

        return rows.stream()
                .map(row -> new MyPtCourseInfo(
                        (String) row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue(),
                        (String) row[3]))
                .toList();
    }
}
