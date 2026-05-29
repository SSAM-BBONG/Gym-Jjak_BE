package com.ssambbong.gymjjak.ptReservation.domain.repository;

import com.ssambbong.gymjjak.ptReservation.domain.model.PtReservation;

import java.time.LocalDateTime;

public interface PtReservationRepository {

    // PT 예약 저장
    PtReservation save(PtReservation ptReservation);

    // 중복 예약 여부 확인. service 에서 중복이면 예외 발생
    boolean existsByPtCourseIdAndTimeOverlap(
            Long ptCourseId,
            LocalDateTime reservedStartedAt,
            LocalDateTime reservedEndAt
    );
}
