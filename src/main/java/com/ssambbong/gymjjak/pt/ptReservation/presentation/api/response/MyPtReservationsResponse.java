package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.LocalDate;
import java.util.List;

public record MyPtReservationsResponse(
        List<PtReservationInfo> ptReservations
) {
    public record PtReservationInfo(
            Long ptReservationId,
            Long thumbnailFileId,
            String title,
            String trainerName,
            PtReservationStatus status,
            LocalDate lastPtDate,
            int progressCount,
            int totalSessionCount
    ) {}

    public static MyPtReservationsResponse from(List<PtReservationQueryUseCase.MyPtReservationView> views) {
        List<PtReservationInfo> infos = views.stream()
                .map(v -> new PtReservationInfo(
                        v.ptReservationId(),
                        v.thumbnailFileId(),
                        v.title(),
                        v.trainerName(),
                        v.status(),
                        v.lastPtDate(),
                        v.progressCount(),
                        v.totalSessionCount()
                ))
                .toList();

        return new MyPtReservationsResponse(infos);
    }
}
