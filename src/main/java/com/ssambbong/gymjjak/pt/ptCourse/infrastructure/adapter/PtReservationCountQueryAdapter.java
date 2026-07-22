package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.PtReservationCountQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 같은 BC(PT) 내 집계 간 접근이므로 SpringDataRepository 직접 사용
@Component
@RequiredArgsConstructor
public class PtReservationCountQueryAdapter implements PtReservationCountQueryPort {

    private final SpringDataPtReservationRepository ptReservationRepository;

    @Override
    public StudentCounts countStudentsByPtCourseIds(List<Long> ptCourseIds) {
        if (ptCourseIds.isEmpty()) return new StudentCounts(Map.of(), Map.of());

        List<Object[]> rows = ptReservationRepository.countStudentsGroupByPtCourseId(ptCourseIds);

        Map<Long, Integer> active = new HashMap<>();
        Map<Long, Integer> total = new HashMap<>();
        ptCourseIds.forEach(id -> { active.put(id, 0); total.put(id, 0); });

        rows.forEach(row -> {
            long courseId = ((Number) row[0]).longValue();
            active.put(courseId, Math.toIntExact(((Number) row[1]).longValue()));
            total.put(courseId, Math.toIntExact(((Number) row[2]).longValue()));
        });

        return new StudentCounts(active, total);
    }
}
