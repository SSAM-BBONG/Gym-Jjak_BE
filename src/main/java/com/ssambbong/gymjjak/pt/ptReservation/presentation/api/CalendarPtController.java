package com.ssambbong.gymjjak.pt.ptReservation.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.pt.ptReservation.application.result.PtReservationCalendarResult;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.FindPtReservationCalendarUsecase;
import com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response.PtReservationCalendarResponse;
import com.ssambbong.gymjjak.pt.ptReservation.presentation.api.response.PtReservationResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pt/reservations")
@RequiredArgsConstructor
@Validated
public class CalendarPtController {

    private final FindPtReservationCalendarUsecase findPtReservationCalendar;

    @GetMapping("/calendar")
    @Operation(summary = "캘린더 월별 조회", description = "일정을 월별로 조회한다.")
    public ResponseEntity<GlobalApiResponse<PtReservationCalendarResponse>> findPtReservationCalendar(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam @Min(1) int year,
            @RequestParam @Min(1) @Max(12) int month
    ) {
        List<PtReservationCalendarResult> results =
                findPtReservationCalendar.findPtReservationCalendar(
                        authUser.userId(),
                        year,
                        month
                );

        PtReservationCalendarResponse response = PtReservationCalendarResponse.of(
                year,
                month,
                results
        );

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        PtReservationResponseCode.PT_RESERVATION_CALENDAR_FETCHED,
                        response
                )
        );
    }
}
