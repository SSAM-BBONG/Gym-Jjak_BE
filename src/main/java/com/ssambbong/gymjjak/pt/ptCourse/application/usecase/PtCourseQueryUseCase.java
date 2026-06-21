package com.ssambbong.gymjjak.pt.ptCourse.application.usecase;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface PtCourseQueryUseCase {

    // 목록 조회
    List<PtCourseListView> findAllPtCourses();

    // 상세 조회
    PtCourseDetailView findPtCourseDetail(Long ptCourseId);

    // 내 강습 목록 조회 (트레이너 전용)
    List<MyPtCourseListView> findMyPtCourses(Long userId, PtCourseStatus status);

    // ──── 목록 뷰 ────
    record PtCourseListView(
            Long ptCourseId,
            String title,
            Long thumbnailFileId,
            int price,
            // 태그
            Long tagId,
            String tagName,
            // 카테고리
            Long categoryId,
            String categoryName,
            // 트레이너
            String trainerName,
            // 조직
            Long organizationId,
            String organizationBusinessName,
            String organizationRoadAddress,
            Double latitude,
            Double longitude,
            // 리뷰
            int reviewCount
    ) {}

    // ──── 상세 뷰 ────
    record PtCourseDetailView(
            Long ptCourseId,
            Long thumbnailFileId,
            String title,
            String description,
            int price,
            int totalSessionCount,
            Double averageRating,
            int reviewCount,
            // 조직
            Long organizationId,
            // 트레이너
            Long trainerProfileId,
            String trainerName,
            Long trainerProfileFileId,
            String trainerIntroduction,
            List<String> certifications,
            List<String> awards,
            List<CurriculumView> curriculums,
            List<ScheduleView> schedules,
            // 미구현 (빈 배열 반환)
            List<Object> recentReviews
    ) {}

    // ──── 커리큘럼 뷰 ────
    record CurriculumView(
            Long curriculumId,
            int sessionNo,
            String title,
            String content
    ) {}

    // ──── 스케쥴 뷰 ────
    record ScheduleView(
            Long scheduleId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    ) {}

    // ──── 내 강습 목록 뷰 ────
    record MyPtCourseListView(
            Long ptCourseId,
            Long thumbnailFileId,
            String title,
            String trainerName,
            PtCourseStatus status,
            int activeReservationCount,   // RESERVED + IN_PROGRESS 수
            int totalReservationCount     // 전체 예약 수
    ) {}
}
