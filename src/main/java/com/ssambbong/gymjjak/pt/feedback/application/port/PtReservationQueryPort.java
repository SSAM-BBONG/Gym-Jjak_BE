package com.ssambbong.gymjjak.pt.feedback.application.port;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.LocalDateTime;

// 피드백 서비스는 '예약 정보'를 조회해야 함

public interface PtReservationQueryPort {

    ReservationInfo findById(Long ptReservationId);

    record ReservationInfo(
            Long ptCourseId,
            Long trainerProfileId,
            Long userId,
            PtReservationStatus status,
            LocalDateTime reservedEndAt
    ) {}
}
