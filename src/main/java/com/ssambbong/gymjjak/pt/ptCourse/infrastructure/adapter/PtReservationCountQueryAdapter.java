package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.PtReservationCountQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
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
    public Map<Long, Integer> countActiveByPtCourseIds(List<Long> ptCourseIds) {
        if (ptCourseIds.isEmpty()) return Map.of();

        List<Object[]> rows = ptReservationRepository.countActiveGroupByPtCourseId(
                ptCourseIds,
                List.of(PtReservationStatus.RESERVED, PtReservationStatus.IN_PROGRESS)
        );

        // 예약이 없는 강습도 0으로 초기화
        Map<Long, Integer> result = new HashMap<>();
        ptCourseIds.forEach(id -> result.put(id, 0));
        rows.forEach(row ->
                result.put(((Number) row[0]).longValue(), Math.toIntExact(((Number) row[1]).longValue()))
        );
        return result;
    }

    @Override
    public Map<Long, Integer> countTotalByPtCourseIds(List<Long> ptCourseIds) {
        if (ptCourseIds.isEmpty()) return Map.of();

        List<Object[]> rows = ptReservationRepository.countTotalGroupByPtCourseId(ptCourseIds);

        Map<Long, Integer> result = new HashMap<>();
        ptCourseIds.forEach(id -> result.put(id, 0));
        rows.forEach(row ->
                result.put(((Number) row[0]).longValue(), Math.toIntExact(((Number) row[1]).longValue()))
        );
        return result;
    }
}
