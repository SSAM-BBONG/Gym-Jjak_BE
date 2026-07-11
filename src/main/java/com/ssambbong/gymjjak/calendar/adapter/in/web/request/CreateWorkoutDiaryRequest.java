package com.ssambbong.gymjjak.calendar.adapter.in.web.request;

import com.ssambbong.gymjjak.calendar.application.command.CreateWorkoutDiaryCommand;
import com.ssambbong.gymjjak.calendar.application.command.WorkoutDiarySetCommand;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Workout diary creation request")
public record CreateWorkoutDiaryRequest(

        @Schema(description = "Diary date", example = "2026-06-24")
        @NotNull(message = "Diary date is required.")
        LocalDate diaryDate,

        @Schema(description = "Workout part", example = "LEG")
        @NotNull(message = "Workout part is required.")
        PartType part,

        @Schema(description = "Exercise name", example = "Wide squat")
        @NotBlank(message = "Exercise name is required.")
        @Size(max = 100, message = "Exercise name must be 100 characters or less.")
        String exercise,

        @Schema(description = "Workout sets")
        @Valid
        @NotEmpty(message = "At least one workout set is required.")
        List<@Valid @NotNull(message = "Workout set is required.") WorkoutDiarySetRequest> sets
) {
    public CreateWorkoutDiaryCommand toCommand() {
        return new CreateWorkoutDiaryCommand(
                diaryDate,
                part,
                exercise,
                sets.stream()
                        .map(WorkoutDiarySetRequest::toCommand)
                        .toList()
        );
    }

    public record WorkoutDiarySetRequest(

            @Schema(description = "Set order", example = "1")
            @NotNull(message = "Set order is required.")
            @Min(value = 1, message = "Set order must be greater than 0.")
            Integer setOrder,

            @Schema(description = "Weight in kg", example = "20")
            @NotNull(message = "Weight is required.")
            @DecimalMin(value = "0.0", message = "Weight must be 0 or greater.")
            BigDecimal weight,

            @Schema(description = "Reps", example = "3")
            @NotNull(message = "Reps is required.")
            @Min(value = 1, message = "Reps must be greater than 0.")
            Integer reps
    ) {
        private WorkoutDiarySetCommand toCommand() {
            return new WorkoutDiarySetCommand(setOrder, weight, reps);
        }
    }
}
