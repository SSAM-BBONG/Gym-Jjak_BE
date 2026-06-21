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
        List<SpringDataFeedbackRepository.LastFeedbackRow> rows =
                feedbackRepository.findLastCreatedAtGroupByReservationId(reservationIds);

        // 타입 안전 프로젝션으로 reservationId → lastPtDate 변환
        return rows.stream().collect(Collectors.toMap(
                SpringDataFeedbackRepository.LastFeedbackRow::getPtReservationId,
                row -> row.getLastCreatedAt().toLocalDate()
        ));
    }
}
