package com.ssambbong.gymjjak.pt.ptReservation.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CreatePtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationCommandUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.presentation.api.request.CreatePtReservationRequest;
import com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response.CreatePtReservationResponse;
import com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response.MyPtReservationsResponse;
import com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response.PtReservationResponseCode;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class PtReservationController {

    private final PtReservationCommandUseCase ptReservationCommandUseCase;
    private final PtReservationQueryUseCase ptReservationQueryUseCase;

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "PT 예약", description = "사용자가 PT 강습을 예약한다.")
    @PostMapping("/pt-courses/{ptCourseId}/reservations")
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
                        new CreatePtReservationResponse(
                                reservationId,
                                PtReservationStatus.RESERVED
                        )
                        ));
    }

    @GetMapping("/reservations/me")
    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "내 PT 예약 목록 조회")
    public ResponseEntity<GlobalApiResponse<MyPtReservationsResponse>> findMyReservations(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(required = false) PtReservationStatus status
    ) {
        var views = ptReservationQueryUseCase.findMyReservations(
                authUser.userId(),
                status
        );

        var response = MyPtReservationsResponse.from(views);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        PtReservationResponseCode.MY_PT_RECORDS_FETCHED,
                        response
                )
        );
    }
}
