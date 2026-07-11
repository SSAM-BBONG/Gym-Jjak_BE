package com.ssambbong.gymjjak.exercise.adapter.in.web.request;

import com.ssambbong.gymjjak.exercise.application.command.UpdateExerciseCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "운동 종목 수정 요청")
public record UpdateExerciseRequest(

        @Schema(description = "운동 이름", example = "레그 프레스")
        @NotBlank(message = "운동 이름은 필수입니다.")
        @Size(max = 100, message = "운동 이름은 100자 이하로 입력해야 합니다.")
        String exerciseName
) {
    public UpdateExerciseCommand toCommand() {
        return new UpdateExerciseCommand(exerciseName);
    }
}
