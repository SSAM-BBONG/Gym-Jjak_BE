package com.ssambbong.gymjjak.pt.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "PT 강습 등록 요청")
public record CreatePtCourseRequest(

        @Schema(description = "PT 강습 제목", example = "체계적인 가슴 집중 PT")
        @NotBlank
        String title,

        @Schema(description = "PT 강습 소개", example = "가슴 근육 발달에 특화된 12주 프로그램입니다.")
        @NotBlank
        String description,

        @Schema(description = "카테고리 ID", example = "1")
        @NotNull
        Long categoryId,

        @Schema(description = "태그 ID", example = "1")
        @NotNull
        Long tagId,

        @Schema(description = "가격 (1회당)", example = "50000")
        @Min(0)
        int price,

        @Schema(description = "썸네일 이미지 URL")
        String thumbnailUrl,

        @Schema(description = "1회 수업 시간 (분)", example = "60")
        @NotNull
        Integer sessionDuration,

        @Schema(description = "커리큘럼 목록")
        @NotEmpty
        List<CurriculumRequest> curriculums,

        @Schema(description = "수업 시간 목록")
        @NotEmpty
        List<ScheduleRequest> schedules

) {
    public record CurriculumRequest(
            @Schema(description = "회차 제목", example = "기초 자세 교정") @NotBlank String title,
            @Schema(description = "회차 설명", example = "현재 체력 및 목표 설정") String content
    ) {}

    public record ScheduleRequest(
            @Schema(description = "요일", example = "MON") @NotNull String dayOfWeek,
            @Schema(description = "시작 시간", example = "10:00") @NotBlank String startTime,
            @Schema(description = "종료 시간", example = "11:00") @NotBlank String endTime
    ) {}
}