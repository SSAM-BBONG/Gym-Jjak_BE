package com.ssambbong.gymjjak.ptReservation.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.ptReservation.application.command.CreatePtReservationCommand;
import com.ssambbong.gymjjak.ptReservation.application.usecase.PtReservationCommandUseCase;
import com.ssambbong.gymjjak.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.ptReservation.presentation.api.request.CreatePtReservationRequest;
import com.ssambbong.gymjjak.ptReservation.presentation.api.response.CreatePtReservationResponse;
import com.ssambbong.gymjjak.ptReservation.presentation.api.response.PtReservationResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PT 예약", description = "PT 예약 관련 API")
@RestController
@RequestMapping("/api/pt-courses")
@RequiredArgsConstructor
public class PtReservationController {

    private final PtReservationCommandUseCase ptReservationCommandUseCase;

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "PT 예약", description = "사용자가 PT 강습을 예약한다.")
    @PostMapping("/{ptCourseId}/reservations")
    public ResponseEntity<GlobalApiResponse<?>> createPtReservation(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long ptCourseId,                  // 예약할 PT 강습 ID
            @RequestBody @Valid CreatePtReservationRequest request
    ) {
        CreatePtReservationCommand command = new CreatePtReservationCommand(
                authUser.userId(),
                ptCourseId,
                request.reservedStartAt(),
                request.reservedEndAt()
        );

        Long reservationId = ptReservationCommandUseCase.createPtReservation(command);
        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(
                        PtReservationResponseCode.PT_RESERVATION_CREATED,
                        new CreatePtReservationResponse(reservationId, PtReservationStatus.RESERVED)
                        ));
    }
}
