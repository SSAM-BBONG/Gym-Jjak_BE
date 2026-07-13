package com.ssambbong.gymjjak.exercise.adapter.in.web.request;

import com.ssambbong.gymjjak.exercise.application.command.CreateExerciseCommand;
import com.ssambbong.gymjjak.global.presentation.api.common.PartTypeNameMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "운동 종목 등록 요청")
public record CreateExerciseRequest(

        @Schema(description = "운동 부위", example = "하체")
        @NotBlank(message = "운동 부위는 필수입니다.")
        String part,

        @Schema(description = "운동 이름", example = "와이드 스쿼트")
        @NotBlank(message = "운동 이름은 필수입니다.")
        @Size(max = 100, message = "운동 이름은 100자 이하로 입력해야 합니다.")
        String exerciseName
) {
    public CreateExerciseCommand toCommand() {
        return new CreateExerciseCommand(
                PartTypeNameMapper.fromKoreanName(part),
                exerciseName
        );
    }
}
