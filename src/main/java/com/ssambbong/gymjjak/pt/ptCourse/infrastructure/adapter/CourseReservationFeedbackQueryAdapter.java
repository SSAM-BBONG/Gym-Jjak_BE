package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence.SpringDataFeedbackRepository;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.CourseReservationFeedbackQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseReservationFeedbackQueryAdapter implements CourseReservationFeedbackQueryPort {

    private final SpringDataFeedbackRepository feedbackRepository;


    @Override
    public Map<Long, LocalDate> findLastFeedbackDatesByReservationIds(List<Long> reservationIds) {
        if (reservationIds.isEmpty()) return Map.of();

        // 예약 ID 목록으로 최근 피드백 날짜 배치 조회 (N+1 방지)
        List<Object[]> rows = feedbackRepository.findLastCreatedAtGroupByReservationId(reservationIds);

        // [reservationId, MAX(createdAt)] → Map<reservationId, LocalDate> 변환
        return rows.stream().collect(Collectors.toMap(
                row -> ((Number) row[0]).longValue(),
                row -> ((java.time.LocalDateTime) row[1]).toLocalDate()
        ));
    }
}
