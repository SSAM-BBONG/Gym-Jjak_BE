package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.ReviewQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

// PT 강습 상세 응답 DTO
public record PtCourseDetailResponse(

        @Schema(description = "PT 강습 ID", example = "1")
        Long ptCourseId,

        @Schema(description = "썸네일 이미지 URL")
        String thumbnailUrl,

        @Schema(description = "제목", example = "맞춤 PT 1개월 과정")
        String title,

        @Schema(description = "소개")
        String description,

        @Schema(description = "가격", example = "345000")
        int price,

        @Schema(description = "전체 회차 수", example = "12")
        int totalSessionCount,

        @Schema(description = "조직 ID", example = "1")
        Long organizationId,

        @Schema(description = "트레이너 프로필 ID", example = "1")
        Long trainerProfileId,

        @Schema(description = "커리큘럼 목록")
        List<CurriculumInfo> curriculums,

        @Schema(description = "수업 시간 목록")
        List<ScheduleInfo> schedules,

        @Schema(description = "최근 강사평 목록 (최대 3개)")
        List<ReviewQueryPort.ReviewSummary> recentReviews

) {

    public record CurriculumInfo(
            @Schema(description = "커리큘럼 ID", example = "1")
            Long curriculumId,
            @Schema(description = "회차 번호", example = "1")
            int sessionNo,
            @Schema(description = "회차 제목", example = "기초 체력 평가 및 목표 설정")
            String title,
            @Schema(description = "회차 설명", example = "체력 측정 및 개인 목표 설정")
            String content
    ) {}

    public record ScheduleInfo(
            @Schema(description = "스케줄 ID", example = "1")
            Long scheduleId,
            @Schema(description = "요일", example = "MONDAY")
            DayOfWeek dayOfWeek,
            @Schema(description = "시작 시간", example = "10:00")
            @JsonFormat(pattern = "HH:mm")
            LocalTime startTime,
            @Schema(description = "종료 시간", example = "11:00")
            @JsonFormat(pattern = "HH:mm")
            LocalTime endTime
    ) {}

    public static PtCourseDetailResponse from(PtCourseQueryUseCase.PtCourseDetailView view) {

        List<CurriculumInfo> curriculums = view.curriculums().stream()
                .map(c -> new CurriculumInfo(c.curriculumId(), c.sessionNo(), c.title(), c.content()))
                .toList();

        List<ScheduleInfo> schedules = view.schedules().stream()
                .map(s -> new ScheduleInfo(s.scheduleId(), s.dayOfWeek(), s.startTime(), s.endTime()))
                .toList();

        return new PtCourseDetailResponse(
                view.ptCourseId(),
                view.thumbnailUrl(),
                view.title(),
                view.description(),
                view.price(),
                view.totalSessionCount(),
                view.organizationId(),
                view.trainerProfileId(),
                curriculums,
                schedules,
                view.recentReviews()
        );
    }
}
