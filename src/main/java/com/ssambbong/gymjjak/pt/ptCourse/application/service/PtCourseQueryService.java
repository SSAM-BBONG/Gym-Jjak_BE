package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.category.application.usecase.CategoryQueryUseCase;
import com.ssambbong.gymjjak.file.application.result.FileUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import com.ssambbong.gymjjak.global.infrastructure.aop.Monitored;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.*;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.dto.TrainerSummaryInfo;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.*;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationNotFoundException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import com.ssambbong.gymjjak.tag.application.usecase.TagQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PtCourseQueryService implements PtCourseQueryUseCase {

    private final PtCourseRepository ptCourseRepository;
    private final PtCurriculumRepository ptCurriculumRepository;
    private final PtCourseScheduleRepository ptCourseScheduleRepository;
    private final CategoryQueryUseCase categoryQueryUseCase;
    private final OrganizationQueryPort organizationQueryPort;
    private final TrainerProfileQueryPort trainerProfileQueryPort;
    private final PtReservationCountQueryPort ptReservationCountQueryPort;
    private final PtReservationRepository ptReservationRepository;
    private final UserNicknameQueryPort userNicknameQueryPort;
    private final CourseReservationFeedbackQueryPort courseReservationFeedbackQueryPort;
    private final TagQueryUseCase tagQueryUseCase;
    private final ReviewQueryPort reviewQueryPort;
    private final FileUrlUseCase fileUrlUseCase;

    @Override
    @Monitored(name = "gymjjak.pt.course.query.duration", domain = "pt_course", action = "find_all")
    public List<PtCourseListView> findAllPtCourses() {
        log.debug("event=pt_courses_find_all");

        Map<Long, String> categoryMap = buildCategoryMap();
        Map<Long, String> tagMap = buildTagMap();

        List<PtCourse> courses = ptCourseRepository.findAllVisible();
        if (courses.isEmpty()) return List.of();

        List<Long> orgIds     = courses.stream().map(PtCourse::getOrganizationId).distinct().toList();
        List<Long> trainerIds = courses.stream().map(PtCourse::getTrainerProfileId).distinct().toList();

        Map<Long, OrganizationQueryPort.OrganizationInfo> orgMap         = organizationQueryPort.findAllByIds(orgIds);
        Map<Long, TrainerSummaryInfo> trainerMap = trainerProfileQueryPort.findSummaryAllByIds(trainerIds);

        List<PtCourseListView> result = courses.stream()
                .map(c -> {
                    OrganizationQueryPort.OrganizationInfo org = orgMap.get(c.getOrganizationId());
                    TrainerSummaryInfo trainer = trainerMap.get(c.getTrainerProfileId());
                    return new PtCourseListView(
                            c.getId(),
                            c.getTitle(),
                            resolveThumbnailUrl(c.getThumbnailFileId()),
                            c.getPrice(),
                            c.getTagId(),
                            tagMap.getOrDefault(c.getTagId(), null),
                            c.getCategoryId(),
                            categoryMap.getOrDefault(c.getCategoryId(), null),
                            trainer != null ? trainer.trainerName() : null,
                            org != null ? org.organizationId() : null,
                            org != null ? org.businessName() : null,
                            org != null ? org.roadAddress() : null,
                            org != null ? org.latitude() : null,
                            org != null ? org.longitude() : null,
                            trainer != null ? trainer.averageRating() : null,
                            trainer != null ? trainer.reviewCount() : 0
                    );
                })
                .toList();

        log.info("event=pt_courses_find_all_succeeded count={}", result.size());
        return result;
    }

    @Override
    public PtCourseDetailView findPtCourseDetail(Long ptCourseId) {
        log.debug("event=pt_course_detail_find ptCourseId={}", ptCourseId);

        PtCourse ptCourse = ptCourseRepository.findById(ptCourseId)
                .orElseThrow(PtCourseNotFoundException::new);

        if (ptCourse.getStatus() != PtCourseStatus.VISIBLE) {
            log.warn("event=pt_course_detail_find_failed reason=not_visible ptCourseId={} status={}", ptCourseId, ptCourse.getStatus());
            throw new PtCourseNotFoundException();
        }

        log.info("event=pt_course_detail_find_succeeded ptCourseId={}", ptCourseId);
        return toDetailView(ptCourse);
    }

    @Override
    public List<MyPtCourseListView> findMyPtCourses(Long userId, PtCourseStatus status) {
        log.debug("event=pt_my_courses_find userId={}, status={}", userId, status);

        // null·VISIBLE·HIDDEN만 허용 (enum 추가 시 의도치 않은 통과 방지)
        if (status != null && status != PtCourseStatus.VISIBLE && status != PtCourseStatus.HIDDEN) {
            log.warn("event=pt_my_courses_find_failed reason=invalid_status userId={} status={}", userId, status);
            throw new PtCourseStatusInvalidException();
        }

        // 로그인한 userId로 트레이너 프로필 ID 조회
        Long trainerProfileId;
        try {
            trainerProfileId = trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(userId);
        } catch (TrainerProfileNotFoundException e) {
            log.warn("event=pt_my_courses_find_failed reason=trainer_not_found, userId={}", userId);
            throw e;
        }

        // 같은 트레이너의 강습이므로 trainerName은 1회만 조회 후 전체 카드에 재사용
        String trainerName = trainerProfileQueryPort
                .findTrainerNameById(trainerProfileId);

        // status=null이면 VISIBLE+HIDDEN 전체, 지정 시 해당 status만
        List<PtCourse> courses = ptCourseRepository
                .findAllByTrainerProfileId(trainerProfileId, status);

        // 강습 ID 목록으로 예약 수를 한 번에 집계 (N+1 방지)
        List<Long> courseIds = courses.stream().map(PtCourse::getId).toList();
        Map<Long, Integer> activeCounts = ptReservationCountQueryPort.countActiveByPtCourseIds(courseIds);
        Map<Long, Integer> totalCounts = ptReservationCountQueryPort.countTotalByPtCourseIds(courseIds);

        List<MyPtCourseListView> result = courses.stream()
                .map(course -> new MyPtCourseListView(
                        course.getId(),
                        resolveThumbnailUrl(course.getThumbnailFileId()),
                        course.getTitle(),
                        trainerName,
                        course.getStatus(),
                        activeCounts.getOrDefault(course.getId(), 0),
                        totalCounts.getOrDefault(course.getId(), 0)
                ))
                .toList();

        log.info("event=pt_my_courses_find_succeeded userId={}, count={}", userId, result.size());
        return result;
    }

    @Override
    public CourseReservationListView findCourseReservations(Long userId, Long ptCourseId) {
        log.debug("event=pt_course_reservations_find userId={}, ptCourseId={}", userId, ptCourseId);

        // 강습 존재 여부 확인
        PtCourse ptCourse = ptCourseRepository.findById(ptCourseId)
                .orElseThrow(() -> {
                    log.warn("event=pt_course_reservations_find_failed reason=course_not_found ptCourseId={}", ptCourseId);
                    return new PtCourseNotFoundException();
                });

        // 본인 강습 여부 확인 (트레이너 프로필 ID 비교)
        Long trainerProfileId;
        try {
            trainerProfileId = trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(userId);
        } catch (TrainerProfileNotFoundException e) {
            log.warn("event=pt_course_reservations_find_failed reason=trainer_not_found userId={}", userId);
            throw e;
        }
        if (!ptCourse.getTrainerProfileId().equals(trainerProfileId)) {
            log.warn("event=pt_course_reservations_find_failed reason=forbidden userId={}, ptCourseId={}", userId, ptCourseId);
            throw new PtCourseForbiddenException();
        }

        // 강습에 속한 예약 전체 조회
        List<PtReservation> reservations = ptReservationRepository.findAllByPtCourseId(ptCourseId);

        // userId 목록으로 닉네임 한 번에 조회 (N+1 방지)
        List<Long> userIds = reservations.stream().map(PtReservation::getUserId).toList();
        Map<Long, String> nicknameMap = userNicknameQueryPort.findNicknamesByUserIds(userIds);

        // 예약 ID 목록으로 마지막 피드백 날짜 한 번에 조회 (N+1 방지)
        List<Long> reservationIds = reservations.stream().map(PtReservation::getId).toList();
        Map<Long, LocalDate> lastFeedbackDateMap =
                courseReservationFeedbackQueryPort.findLastFeedbackDatesByReservationIds(reservationIds);

        List<CourseReservationView> reservationViews = reservations.stream()
                .map(r -> new CourseReservationView(
                        r.getId(),
                        nicknameMap.getOrDefault(r.getUserId(), null),
                        r.getStatus(),
                        lastFeedbackDateMap.getOrDefault(r.getId(), null), // 피드백 없으면 null
                        r.getProgressCount(),
                        r.getTotalSessionCount()
                ))
                .toList();

        log.info("event=pt_course_reservations_find_succeeded ptCourseId={}, count={}",
                ptCourseId, reservationViews.size());

        return new CourseReservationListView(ptCourse.getTitle(), reservationViews);
    }

    @Override
    public ReservationDetailView findReservationDetail(Long userId, Long ptReservationId) {
        log.debug("event=pt_reservation_detail_find userId={}, ptReservationId={}", userId, ptReservationId);

        // 예약 존재 여부 확인
        PtReservation reservation = ptReservationRepository.findById(ptReservationId)
                .orElseThrow(() -> {
                    log.warn("event=pt_reservation_detail_find_failed reason=reservation_not_found ptReservationId={}", ptReservationId);
                    return new PtReservationNotFoundException();
                });

        // 예약에서 ptCourseId 추출 후 강습 조회
        PtCourse ptCourse = ptCourseRepository.findById(reservation.getPtCourseId())
                .orElseThrow(PtCourseNotFoundException::new);

        // 트레이너 프로필 조회
        Long trainerProfileId;
        try {
            trainerProfileId = trainerProfileQueryPort.findActiveTrainerProfileIdByUserId(userId);
        } catch (TrainerProfileNotFoundException e) {
            log.warn("event=pt_reservation_detail_find_failed reason=trainer_not_found userId={}", userId);
            throw e;
        }

        // 본인 강습 여부 확인
        if (!ptCourse.getTrainerProfileId().equals(trainerProfileId)) {
            log.warn("event=pt_reservation_detail_find_failed reason=forbidden userId={}, ptReservationId={}", userId, ptReservationId);
            throw new PtCourseForbiddenException();
        }

        // 수강생 프로필 조회 (nickname, email, phone)
        UserNicknameQueryPort.StudentProfile studentProfile =
                userNicknameQueryPort.findUserDetail(reservation.getUserId())
                        .orElseThrow(() -> {
                            log.warn("event=pt_reservation_detail_find_failed reason=student_not_found userId={}", reservation.getUserId());
                            return new StudentNotFoundException();
                        });

        log.info("event=pt_reservation_detail_find_succeeded ptReservationId={}", ptReservationId);

        return new ReservationDetailView(
                studentProfile.nickname(),
                studentProfile.email(),
                studentProfile.phone(),
                reservation.getStatus(),
                reservation.getProgressCount(),
                reservation.getTotalSessionCount(),
                ptCourse.getTitle()
        );
    }

    // PT 통계 조회
    @Override
    public PtStatsView findStats() {
        log.debug("event=pt_stats_find");

        long organizationCount = organizationQueryPort.countActive();
        long activeTrainerCount = trainerProfileQueryPort.countActive();
        long inProgressPtCount = ptReservationRepository.countByStatus(PtReservationStatus.IN_PROGRESS);
        Double averageSatisfaction = trainerProfileQueryPort.averageRating();

        log.info("event=pt_stats_find_succeeded organizationCount={}, activeTrainerCount={}, inProgressPtCount={}, averageSatisfaction={}",
                organizationCount, activeTrainerCount, inProgressPtCount, averageSatisfaction);

        return new PtStatsView(organizationCount, activeTrainerCount, inProgressPtCount, averageSatisfaction);
    }

    // 인기 강습 조회
    @Monitored(name = "gymjjak.pt.course.query.duration", domain = "pt_course", action = "find_popular")
    @Override
    public List<PopularCourseView> findPopular() {
        log.debug("event=pt_courses_popular_find");

        // 태그 ID → 이름 매핑 (N+1 방지)
        Map<Long, String> tagMap = tagQueryUseCase.handle().stream()
                .collect(Collectors.toMap(
                        TagQueryUseCase.TagView::tagId,
                        TagQueryUseCase.TagView::name
                ));

        Map<Long, String> categoryMap = buildCategoryMap();

        List<PopularCourseView> result = ptCourseRepository.findPopular(4).stream()
                .map(ptCourse -> {
                    OrganizationQueryPort.OrganizationInfo org =
                            organizationQueryPort.findById(ptCourse.getOrganizationId());
                    String trainerName;
                    try {
                        trainerName = trainerProfileQueryPort.findTrainerNameById(ptCourse.getTrainerProfileId());
                    } catch (Exception e) {
                        log.warn("event=pt_course_popular_trainer_not_found trainerProfileId={}", ptCourse.getTrainerProfileId());
                        trainerName = null;
                    }

                    return new PopularCourseView(
                            ptCourse.getId(),
                            ptCourse.getTitle(),
                            ptCourse.getPrice(),
                            resolveThumbnailUrl(ptCourse.getThumbnailFileId()),
                            ptCourse.getCategoryId(),
                            categoryMap.getOrDefault(ptCourse.getCategoryId(), null),
                            ptCourse.getTagId(),
                            tagMap.getOrDefault(ptCourse.getTagId(), null),
                            trainerName,
                            org.roadAddress()
                    );
                })
                .toList();

        log.info("event=pt_courses_popular_find_succeeded count={}", result.size());
        return result;
    }

    // categoryId -> categoryName 매핑
    private Map<Long, String> buildCategoryMap() {
        return categoryQueryUseCase.handle().stream()
                .collect(Collectors.toMap(
                        CategoryQueryUseCase.CategoryView::categoryId,
                        CategoryQueryUseCase.CategoryView::name
                ));
    }

    // tagId -> tagName 매핑
    private Map<Long, String> buildTagMap() {
        return tagQueryUseCase.handle().stream()
                .collect(Collectors.toMap(
                        TagQueryUseCase.TagView::tagId,
                        TagQueryUseCase.TagView::name
                ));
    }

    // PT_THUMBNAIL은 public 파일 → requesterId 없이 URL 반환, 없으면 null
    private String resolveThumbnailUrl(Long fileId) {
        if (fileId == null) return null;
        try {
            FileUrlResult file = fileUrlUseCase.getUrl(fileId, null, false);
            return file.url();
        } catch (FileNotFoundException e) {
            log.warn("event=pt_course_thumbnail_not_found fileId={}", fileId);
            return null;
        } catch (RuntimeException e) {
            log.error("event=pt_course_thumbnail_url_resolve_failed fileId={}", fileId, e);
            return null;
        }
    }

    // ptCourse + 커리큘럼/스케쥴 목록 -> 상세 응답용 View 반환
    private PtCourseDetailView toDetailView(PtCourse ptCourse) {
        // 커리큘럼 조회 (도메인 모델 -> View 변환)
        List<CurriculumView> curriculums = ptCurriculumRepository.findAllByPtCourseId(ptCourse.getId()).stream()
                .map(c -> new CurriculumView(c.getId(), c.getSessionNo(), c.getTitle(), c.getContent()))
                .toList();
        log.debug("event=pt_course_detail_find ptCourseId={} curriculum_count={}", ptCourse.getId(), curriculums.size());

        // 스케쥴 조회 (도메인 모델 -> View 변환)
        List<ScheduleView> schedules = ptCourseScheduleRepository.findAllByPtCourseId(ptCourse.getId()).stream()
                .map(s -> new ScheduleView(s.getId(), s.getDayOfWeek(), s.getStartTime(), s.getEndTime()))
                .toList();
        log.debug("event=pt_course_detail_find ptCourseId={} schedule_count={}", ptCourse.getId(), schedules.size());

        return new PtCourseDetailView(
                ptCourse.getId(),
                resolveThumbnailUrl(ptCourse.getThumbnailFileId()),
                ptCourse.getTitle(),
                ptCourse.getDescription(),
                ptCourse.getPrice(),
                ptCourse.getTotalSessionCount(),
                ptCourse.getOrganizationId(),
                ptCourse.getTrainerProfileId(),
                curriculums,
                schedules,
                reviewQueryPort.findRecentByTrainerProfileId(ptCourse.getTrainerProfileId(), 3)
        );
    }

    @Override
    public AvailableDatesView findAvailableDates(Long ptCourseId) {
        log.debug("event=pt_course_available_dates_find ptCourseId={}", ptCourseId);
        ptCourseRepository.findById(ptCourseId)
                .orElseThrow(() -> {
                    log.warn("event=pt_course_available_dates_find_failed reason=course_not_found ptCourseId={}", ptCourseId);
                    return new PtCourseNotFoundException();
                });

        List<com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule> schedules =
                ptCourseScheduleRepository.findAllByPtCourseId(ptCourseId);
        if (schedules.isEmpty()) return new AvailableDatesView(List.of());

        LocalDate firstDay = LocalDate.now();
        LocalDate lastDay = firstDay.plusDays(29);

        Set<LocalDateTime> reserved = new HashSet<>(
                ptReservationRepository.findReservedStartAtsByPtCourseId(
                        ptCourseId,
                        firstDay.atStartOfDay(),
                        lastDay.plusDays(1).atStartOfDay()
                )
        );

        LocalDateTime now = LocalDateTime.now();
        List<LocalDate> availableDates = new ArrayList<>();
        LocalDate date = firstDay;
        while (!date.isAfter(lastDay)) {
            final LocalDate d = date;
            boolean hasAvailable = schedules.stream()
                    .filter(s -> s.getDayOfWeek() == d.getDayOfWeek())
                    .anyMatch(s -> {
                        LocalDateTime slotStart = LocalDateTime.of(d, s.getStartTime());
                        return slotStart.isAfter(now) && !reserved.contains(slotStart);
                    });
            if (hasAvailable) availableDates.add(d);
            date = date.plusDays(1);
        }

        log.info("event=pt_course_available_dates_find_succeeded ptCourseId={} from={} to={} count={}",
                ptCourseId, firstDay, lastDay, availableDates.size());
        return new AvailableDatesView(availableDates);
    }

    @Override
    public AvailableTimeSlotsView findAvailableTimeSlots(Long ptCourseId, LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            log.warn("event=pt_course_available_time_slots_failed reason=past_date ptCourseId={} date={}", ptCourseId, date);
            throw new PtCourseInvalidException();
        }
        log.debug("event=pt_course_available_time_slots_find ptCourseId={} date={}", ptCourseId, date);
        ptCourseRepository.findById(ptCourseId)
                .orElseThrow(() -> {
                    log.warn("event=pt_course_available_time_slots_find_failed reason=course_not_found ptCourseId={}", ptCourseId);
                    return new PtCourseNotFoundException();
                });

        Set<LocalDateTime> reserved = new HashSet<>(
                ptReservationRepository.findReservedStartAtsByPtCourseId(
                        ptCourseId,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay()
                )
        );

        LocalDateTime now = LocalDateTime.now();
        List<TimeSlotView> timeSlots = ptCourseScheduleRepository.findAllByPtCourseId(ptCourseId).stream()
                .filter(s -> s.getDayOfWeek() == date.getDayOfWeek())
                .sorted(Comparator.comparing(com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseSchedule::getStartTime))
                .map(s -> {
                    LocalDateTime slotStart = LocalDateTime.of(date, s.getStartTime());
                    boolean available = slotStart.isAfter(now) && !reserved.contains(slotStart);
                    return new TimeSlotView(s.getStartTime(), s.getEndTime(), available);
                })
                .toList();

        log.info("event=pt_course_available_time_slots_find_succeeded ptCourseId={} date={} slots={}",
                ptCourseId, date, timeSlots.size());
        return new AvailableTimeSlotsView(date, timeSlots);
    }
}
