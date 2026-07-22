package com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response;

import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtSessionStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PtSessionsResponse(List<PtSessionItem> sessions) {

    public static PtSessionsResponse from(List<PtReservationQueryUseCase.PtSessionView> views) {
        return new PtSessionsResponse(
                views.stream().map(PtSessionItem::from).toList()
        );
    }

    public record PtSessionItem(
            Long ptReservationId,
            Long ptCourseId,
            String ptCourseTitle,
            String trainerName,
            LocalDateTime reservedStartAt,
            LocalDateTime reservedEndAt,
            PtSessionStatus sessionStatus
    ) {
        public static PtSessionItem from(PtReservationQueryUseCase.PtSessionView view) {
            return new PtSessionItem(
                    view.ptReservationId(),
                    view.ptCourseId(),
                    view.ptCourseTitle(),
                    view.trainerName(),
                    view.reservedStartAt(),
                    view.reservedEndAt(),
                    view.sessionStatus()
            );
        }
    }
}
