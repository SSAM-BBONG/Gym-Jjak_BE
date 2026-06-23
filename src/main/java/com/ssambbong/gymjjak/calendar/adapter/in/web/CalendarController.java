package com.ssambbong.gymjjak.calendar.adapter.in.web;

import com.ssambbong.gymjjak.calendar.adapter.in.web.request.CreateWorkoutDiaryRequest;
import com.ssambbong.gymjjak.calendar.adapter.in.web.response.CalendarResponseCode;
import com.ssambbong.gymjjak.calendar.adapter.in.web.response.CreateWorkoutDiaryResponse;
import com.ssambbong.gymjjak.calendar.application.command.CreateWorkoutDiaryCommand;
import com.ssambbong.gymjjak.calendar.application.port.in.CreateWorkoutDiaryUsecase;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.user.adapter.in.web.response.UserResponseCode;
import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
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

    private final CreateWorkoutDiaryUsecase createWorkoutDiaryUsecase;

    @PostMapping("/diaries")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GlobalApiResponse<Void>> createWorkoutDiary(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateWorkoutDiaryRequest request
    ) {
        createWorkoutDiaryUsecase.createWorkoutDiary(
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
}
