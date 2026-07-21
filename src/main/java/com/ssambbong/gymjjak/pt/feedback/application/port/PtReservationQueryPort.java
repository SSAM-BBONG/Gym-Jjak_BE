package com.ssambbong.gymjjak.pt.feedback.application.port;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// 피드백 서비스는 '예약 정보'를 조회해야 함

public interface PtReservationQueryPort {

    ReservationInfo findById(Long ptReservationId);

    List<Long> findReservationIdsByUserIdAndPtCourseId(Long userId, Long ptCourseId);

    // reservationId → reservedStartAt (피드백 목록 날짜 표시용)
    Map<Long, LocalDate> findReservationStartDatesByUserIdAndPtCourseId(Long userId, Long ptCourseId);

    record ReservationInfo(
            Long ptCourseId,
            Long trainerProfileId,
            Long userId,
            PtReservationStatus status,
            LocalDateTime reservedEndAt
    ) {}
}
