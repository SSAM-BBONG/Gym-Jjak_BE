package com.ssambbong.gymjjak.pt.ptReservation.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CancelPtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.ChangePtReservationStatusCommand;
import com.ssambbong.gymjjak.pt.ptReservation.application.command.CreatePtReservationCommand;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationCommandUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.presentation.api.request.ChangePtReservationStatusRequest;
import com.ssambbong.gymjjak.pt.ptReservation.presentation.api.request.CreatePtReservationRequest;
import com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "내 PT 예약 목록 조회", description = "사용자가 본인이 예약한 PT 강습 목록을 조회한다.")
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

    @GetMapping("/reservations/me/{ptReservationId}")
    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "내 PT 예약 상세 조회", description = "본인의 PT 예약 상세 정보를 조회한다.")
    public ResponseEntity<GlobalApiResponse<MyPtReservationDetailResponse>> findMyReservationDetail(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long ptReservationId   // 조회할 예약 ID
    ) {
        var view = ptReservationQueryUseCase.findMyReservationDetail(
                authUser.userId(),
                ptReservationId
        );

        // View → 응답 DTO 변환
        var response = MyPtReservationDetailResponse.from(view);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        PtReservationResponseCode.MY_PT_RECORD_DETAIL_FETCHED,
                        response
                )
        );
    }

    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "PT 예약 상태 변경", description = "트레이너가 본인 PT 예약 상태를 변경한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = ChangePtReservationStatusResponse.class))),
            @ApiResponse(responseCode = "409", description = "허용되지 않는 상태값 (RESERVED는 설정 불가)", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "본인 예약 아님", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음", content = @Content(schema = @Schema()))
    })
    @PatchMapping("/reservations/{reservationId}/status")
    public ResponseEntity<GlobalApiResponse<ChangePtReservationStatusResponse>> changePtReservationStatus(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long reservationId,
            @RequestBody @Valid ChangePtReservationStatusRequest request
    ) {
        PtReservation reservation = ptReservationCommandUseCase.changePtReservationStatus(
                new ChangePtReservationStatusCommand(authUser.userId(), reservationId, request.status()));
        ChangePtReservationStatusResponse response = new ChangePtReservationStatusResponse(
                reservation.getStatus(),
                reservation.getProgressCount(),
                reservation.getTotalSessionCount()
        );
        return ResponseEntity.ok(GlobalApiResponse.ok(PtReservationResponseCode.PT_RESERVATION_STATUS_UPDATED, response));
    }

    // PT 예약 취소 (수강생 본인만 가능)
    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "PT 예약 취소", description = "수강생이 본인의 PT 예약을 취소한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공",
                    content = @Content(schema = @Schema(implementation = CancelPtReservationResponse.class))),
            @ApiResponse(responseCode = "403", description = "본인 예약 아님",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "취소 불가 상태 (COMPLETED / CANCELLED)",
                    content = @Content(schema = @Schema()))
    })
    @PatchMapping("/reservations/me/{reservationId}/cancel")
    public ResponseEntity<GlobalApiResponse<CancelPtReservationResponse>> cancelPtReservation(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("reservationId") Long ptReservationId
    ) {
        PtReservation reservation = ptReservationCommandUseCase.cancelPtReservation(
                new CancelPtReservationCommand(authUser.userId(), ptReservationId)
        );
        return ResponseEntity.ok(GlobalApiResponse.ok(
                PtReservationResponseCode.PT_RESERVATION_CANCELLED,
                CancelPtReservationResponse.from(reservation)
        ));
    }
}
