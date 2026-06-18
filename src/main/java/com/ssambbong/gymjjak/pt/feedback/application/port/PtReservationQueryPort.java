package com.ssambbong.gymjjak.pt.feedback.application.port;

// 피드백 서비스는 '예약 정보'를 조회해야 함

public interface PtReservationQueryPort {

    ReservationInfo findById(Long ptReservationId);

    record ReservationInfo(
            Long ptCourseId, // 커리큘럼 목록 조회
            Long trainerProfileId, // 트레이너 소유권 검증
            Long userId // 수강생 소유권 검증
    ) {}
}
