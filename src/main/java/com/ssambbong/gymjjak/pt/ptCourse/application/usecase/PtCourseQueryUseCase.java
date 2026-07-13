package com.ssambbong.gymjjak.pt.ptCourse.application.usecase;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.ReviewQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // 예약 가능 날짜 조회 (오늘부터 30일)
    AvailableDatesView findAvailableDates(Long ptCourseId);

    // 예약 가능 시간 슬롯 조회 (날짜 기준)
    AvailableTimeSlotsView findAvailableTimeSlots(Long ptCourseId, LocalDate date);

    // ──── 목록 뷰 ────
    record PtCourseListView(
            Long ptCourseId,
            String title,
            String thumbnailUrl,
            int price,
            // 부위
            PartType part,
            // 트레이너
            String trainerName,
            // 조직
            Long organizationId,
            String organizationBusinessName,
            String organizationRoadAddress,
            Double latitude,
            Double longitude,
            // 리뷰
            Double averageRating,
            int reviewCount,
            LocalDateTime createdAt
    ) {}

    // ──── 상세 뷰 ────
    record PtCourseDetailView(
            Long ptCourseId,
            String thumbnailUrl,
            String title,
            String description,
            int price,
            int totalSessionCount,
            Long organizationId,
            Long trainerProfileId,
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
            String thumbnailUrl,
            String title,
            String trainerName,
            PtCourseStatus status,
            int activeReservationCount,   // 현재 수강 중인 수강생 수
            int totalReservationCount     // 전체 수강생 수 (취소 제외)
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
            LocalDate lastPtDate,      // 가장 최근 완료 회차의 종료일, 없으면 null
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

    // ──── 예약 가능 날짜 뷰 ────
    record AvailableDatesView(List<LocalDate> availableDates) {}

    // ──── 예약 가능 시간 슬롯 뷰 ────
    record AvailableTimeSlotsView(LocalDate date, List<TimeSlotView> timeSlots) {}

    record TimeSlotView(LocalTime startTime, LocalTime endTime, boolean available) {}

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
            String thumbnailUrl,
            PartType part,
            String trainerName,
            String roadAddress
    ) {}
}
