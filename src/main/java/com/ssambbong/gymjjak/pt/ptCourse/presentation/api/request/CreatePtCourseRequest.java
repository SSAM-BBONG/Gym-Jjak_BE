package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

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

        @Schema(description = "썸네일 파일 ID (선택)", example = "1")
        @Positive
        Long thumbnailFileId,

        @Schema(description = "커리큘럼 목록")
        @NotEmpty
        List<CurriculumRequest> curriculums,

        @Schema(description = "수업 시간 목록")
        @NotEmpty
        List<ScheduleRequest> schedules

) {
    public record CurriculumRequest(
            @Schema(description = "회차 번호", example = "1") @NotNull @Min(1) Integer sessionNo,
            @Schema(description = "회차 제목", example = "기초 자세 교정") @NotBlank String title,
            @Schema(description = "회차 설명", example = "현재 체력 및 목표 설정") String content
    ) {}

    public record ScheduleRequest(
            @Schema(description = "요일 (MONDAY~SUNDAY)", example = "MONDAY")
            @NotNull
            @Pattern(regexp = "MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY",
                    message = "요일은 MONDAY~SUNDAY 중 하나여야 합니다.")
            String dayOfWeek,

            @Schema(description = "시작 시간 (HH:mm)", example = "10:00")
            @NotBlank
            @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
                    message = "시작 시간은 HH:mm 형식이어야 합니다.")
            String startTime,

            @Schema(description = "종료 시간 (HH:mm)", example = "11:00")
            @NotBlank
            @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
                    message = "종료 시간은 HH:mm 형식이어야 합니다.")
            String endTime
    ) {}
}