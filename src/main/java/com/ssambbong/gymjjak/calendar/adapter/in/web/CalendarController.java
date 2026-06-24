package com.ssambbong.gymjjak.calendar.adapter.in.web;

import com.ssambbong.gymjjak.calendar.adapter.in.web.request.CreateWorkoutDiaryRequest;
import com.ssambbong.gymjjak.calendar.adapter.in.web.request.UpdateWorkoutDiaryRequest;
import com.ssambbong.gymjjak.calendar.adapter.in.web.response.CalendarResponseCode;
import com.ssambbong.gymjjak.calendar.application.command.CreateWorkoutDiaryCommand;
import com.ssambbong.gymjjak.calendar.application.port.in.WorkoutDiaryUsecase;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
public class CalendarController {

    private final WorkoutDiaryUsecase workoutDiaryUsecase;

    @PostMapping("/diaries")
    @Operation(summary = "운동 일지 작성", description = "운동 일지를 캘린더에 작성한다.")
    public ResponseEntity<GlobalApiResponse<Void>> createWorkoutDiary(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateWorkoutDiaryRequest request
    ) {
        workoutDiaryUsecase.createWorkoutDiary(
                authUser.userId(),
                new CreateWorkoutDiaryCommand(
                        request.diaryDate(),
                        request.categoryName(),
                        request.title(),
                        request.content()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalApiResponse.created(
                        CalendarResponseCode.DIARY_CREATED
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


}
