package com.ssambbong.gymjjak.calendar.adapter.in.web.request;

import com.ssambbong.gymjjak.calendar.application.command.UpdateWorkoutDiaryCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "운동 일지 수정 요청")
public record UpdateWorkoutDiaryRequest(

        @Schema(description = "제목", example = "힘든 운동을 한 날")
        @NotBlank(message = "일지 제목은 필수입니다.")
        @Size(max = 100, message = "일지 제목은 100자 이하로 입력해야 합니다.")
        String title,

        @Schema(description = "내용", example = "힘드노 헥헥 개힘드노 헥헥 살려줘")
        @NotBlank(message = "일지 내용은 필수입니다.")
        String content
) {
    public UpdateWorkoutDiaryCommand toCommand() {
        return new UpdateWorkoutDiaryCommand(title, content);
    }
}
