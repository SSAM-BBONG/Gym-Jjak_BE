package com.ssambbong.gymjjak.pt.ptCourse.application.usecase;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.ReviewQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PtCourseQueryUseCase {

    // 목록 조회
    List<PtCourseListView> findAllPtCourses();

    // 상세 조회
    PtCourseDetailView findPtCourseDetail(Long ptCourseId);

    // 내 강습 목록 조회 (트레이너 전용)
    List<MyPtCourseListView> findMyPtCourses(Long userId, PtCourseStatus status);

    // 강습별 수강생 목록 조회 (트레이너 전용)
    CourseReservationListView findCourseReservations(Long userId, Long ptCourseId);

    // 수강생 상세 조회 (트레이너 전용)
    ReservationDetailView findReservationDetail(Long userId, Long ptReservationId);

    // 인기 강습 조회
    List<PopularCourseView> findPopular();

    // PT 통계 조회
    PtStatsView findStats();

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
            List<ReviewQueryPort.ReviewSummary> recentReviews
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

    // 강습별 수강생 목록 응답 (강습 제목 + 예약 목록)
    record CourseReservationListView(
            String title,
            List<CourseReservationView> ptReservations
    ) {}

    // 수강생 1명의 예약 정보
    record CourseReservationView(
            Long ptReservationId,
            String nickname,
            PtReservationStatus status,
            LocalDate lastPtDate,      // 가장 최근 피드백 날짜, 없으면 null
            int progressCount,
            int totalSessionCount
    ) {}

    // 수강생 상세 뷰
    record ReservationDetailView(
            String nickname,
            String email,
            String phone,
            PtReservationStatus status,
            int progressCount,
            int totalSessionCount,
            String title
    ) {}

    // ──── PT 통계 뷰 ────
    record PtStatsView(
            long organizationCount,
            long activeTrainerCount,
            long inProgressPtCount,
            Double averageSatisfaction
    ) {}

    // ──── 인기 강습 뷰 ────
    record PopularCourseView(
            Long ptCourseId,
            String title,
            int price,
            Long thumbnailFileId,
            Long categoryId,
            String categoryName,
            Long tagId,
            String tagName,
            String trainerName,
            String roadAddress
    ) {}
}
