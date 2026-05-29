package com.ssambbong.gymjjak.ptReservation.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "PT 예약 요청")
public record CreatePtReservationRequest(

        @Schema(description = "예약 시작 시간", example = "2026-06-01T10:00:00")
        @NotNull
        LocalDateTime reservedStartAt,

        @Schema(description = "예약 종료 시간", example = "2026-06-01T11:00:00")
        @NotNull
        LocalDateTime reservedEndAt
) {
}
