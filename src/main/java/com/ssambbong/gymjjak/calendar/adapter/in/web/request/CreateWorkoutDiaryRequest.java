package com.ssambbong.gymjjak.calendar.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "운동 일지 등록 요청")
public record CreateWorkoutDiaryRequest(

        @Schema(description = "날짜", example = "2026-06-23")
        @NotNull(message = "일지 날짜는 필수입니다.")
        LocalDate diaryDate,

        @Schema(description = "제목", example = "오늘의 마라톤")
        @NotBlank(message = "일지 제목은 필수입니다.")
        @Size(max = 100, message = "일지 제목은 100자 이하로 입력해야 합니다.")
        String title,

        @Schema(description = "내용", example = "씨이빠 ㅈㄴ 힘드노 헥헥")
        @NotBlank(message = "일지 내용은 필수입니다.")
        String content
) {
}
