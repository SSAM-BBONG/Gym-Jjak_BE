package com.ssambbong.gymjjak.calendar.adapter.in.web;

import com.ssambbong.gymjjak.calendar.adapter.in.web.request.CreateWorkoutDiaryRequest;
import com.ssambbong.gymjjak.calendar.adapter.in.web.request.UpdateWorkoutDiaryRequest;
import com.ssambbong.gymjjak.calendar.adapter.in.web.response.CalendarDayResponse;
import com.ssambbong.gymjjak.calendar.adapter.in.web.response.CalendarMonthResponse;
import com.ssambbong.gymjjak.calendar.adapter.in.web.response.CalendarResponseCode;
import com.ssambbong.gymjjak.calendar.adapter.in.web.response.CreateWorkoutDiaryResponse;
import com.ssambbong.gymjjak.calendar.application.port.in.CalendarUsecase;
import com.ssambbong.gymjjak.calendar.application.port.in.WorkoutDiaryUsecase;
import com.ssambbong.gymjjak.calendar.application.result.CalendarDayResult;
import com.ssambbong.gymjjak.calendar.application.result.CalendarMonthResult;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
public class CalendarController {

    private final WorkoutDiaryUsecase workoutDiaryUsecase;
    private final CalendarUsecase calendarUsecase;

    @PostMapping("/diaries")
    @Operation(summary = "운동 일지 작성", description = "운동 일지를 캘린더에 작성한다.")
    public ResponseEntity<GlobalApiResponse<CreateWorkoutDiaryResponse>> createWorkoutDiary(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateWorkoutDiaryRequest request
    ) {
        Long workoutDiaryId = workoutDiaryUsecase.createWorkoutDiary(
                authUser.userId(),
                request.toCommand()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalApiResponse.created(
                        CalendarResponseCode.DIARY_CREATED,
                        CreateWorkoutDiaryResponse.from(workoutDiaryId)
                ));
    }

    @PatchMapping("/diaries/{workoutDiaryId}")
    @Operation(summary = "운동 일지 수정", description = "운동 일지를 수정한다.")
    public ResponseEntity<GlobalApiResponse<Void>> updateWorkoutDiary(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long workoutDiaryId,
            @Valid @RequestBody UpdateWorkoutDiaryRequest request
    ) {
        workoutDiaryUsecase.updateWorkoutDiary(
                authUser.userId(),
                workoutDiaryId,
                request.toCommand()
        );

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        CalendarResponseCode.DIARY_UPDATED)
        );
    }

    @DeleteMapping("/diaries/{workoutDiaryId}")
    @Operation(summary = "운동 일지 삭제", description = "운동 일지를 삭제한다.")
    public ResponseEntity<GlobalApiResponse<Void>> deleteWorkoutDiary(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long workoutDiaryId
    ) {
        workoutDiaryUsecase.deleteWorkoutDiary(
                authUser.userId(),
                workoutDiaryId
        );

        return ResponseEntity.ok(
                GlobalApiResponse.ok(CalendarResponseCode.DIARY_DELETED)
        );
    }

    @GetMapping("/day")
    @Operation(summary = "캘린더 일별 조회", description = "캘린더를 일별로 조회한다.")
    public ResponseEntity<GlobalApiResponse<CalendarDayResponse>> findCalendarDay(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        CalendarDayResult result = calendarUsecase.findCalendarDay(
                authUser.userId(),
                date
        );

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        CalendarResponseCode.CALENDAR_DAY_FETCHED,
                        CalendarDayResponse.from(result)
                )
        );
    }

    @GetMapping("/month")
    @Operation(summary = "캘린더 월별 조회", description = "캘린더를 월별로 조회한다.")
    public ResponseEntity<GlobalApiResponse<CalendarMonthResponse>> findCalendarMonth(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        CalendarMonthResult result = calendarUsecase.findCalendarMonth(
                authUser.userId(),
                year,
                month
        );

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        CalendarResponseCode.CALENDAR_FETCHED,
                        CalendarMonthResponse.from(result)
                )
        );
    }


}
