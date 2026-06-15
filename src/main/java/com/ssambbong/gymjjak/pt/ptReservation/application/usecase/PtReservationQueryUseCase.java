package com.ssambbong.gymjjak.pt.ptReservation.application.usecase;

import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.LocalDate;
import java.util.List;

public interface PtReservationQueryUseCase {

    // 내 PT 예약 기록 목록 조회 - status null이면 전체 조회
    List<MyPtReservationView> findMyReservations(Long userId, PtReservationStatus status);

    record MyPtReservationView(
            Long ptReservationId,
            Long thumbnailFileId,
            String title,
            String trainerName,
            PtReservationStatus status,
            LocalDate lastPtDate,
            int progressCount,
            int totalSessionCount
    ) {}
}
