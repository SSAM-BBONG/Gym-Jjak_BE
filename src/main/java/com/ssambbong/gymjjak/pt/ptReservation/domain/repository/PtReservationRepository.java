package com.ssambbong.gymjjak.pt.ptReservation.domain.repository;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PtReservationRepository {

    // PT 예약 저장
    PtReservation save(PtReservation ptReservation);

    // 중복 예약 여부 확인. service 에서 중복이면 예외 발생
    boolean existsByPtCourseIdAndTimeOverlap(
            Long ptCourseId,
            LocalDateTime reservedStartedAt,
            LocalDateTime reservedEndAt
    );

    // status null이면 전체 조회
    List<PtReservation> findAllByUserId(Long userId, PtReservationStatus status);

    // 예약 1건 상세 조회 (본인 확인용)
    Optional<PtReservation> findById(Long id);
}
