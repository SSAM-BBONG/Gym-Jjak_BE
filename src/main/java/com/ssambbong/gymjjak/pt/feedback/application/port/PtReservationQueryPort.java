package com.ssambbong.gymjjak.pt.feedback.application.port;

// 피드백 서비스는 '예약 정보'를 조회해야 함

public interface PtReservationQueryPort {

    ReservationInfo findById(Long ptReservationId);

    record ReservationInfo(
            Long ptCourseId,
            Long trainerProfileId,
            Long userId,
            String status
    ) {}
}
