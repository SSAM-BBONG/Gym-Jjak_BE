package com.ssambbong.gymjjak.pt.ptCourse.application.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PtCourseQueryService implements PtCourseQueryUseCase {

    private final PtCourseRepository ptCourseRepository;
    private final PtCurriculumRepository ptCurriculumRepository;
    private final PtCourseScheduleRepository ptCourseScheduleRepository;
    private final OrganizationQueryPort organizationQueryPort;
    private final TrainerProfileQueryPort trainerProfileQueryPort;
    private final PtReservationCountQueryPort ptReservationCountQueryPort;
    private final PtReservationRepository ptReservationRepository;
    private final UserNicknameQueryPort userNicknameQueryPort;
    private final ReviewQueryPort reviewQueryPort;
    private final FileUrlUseCase fileUrlUseCase;

    @Override
    @Cacheable(value = "ptCourseList", sync = true)
    @Monitored(name = "gymjjak.pt.course.query.duration", domain = "pt_course", action = "find_all")
    public List<PtCourseListView> findAllPtCourses() {
        log.debug("event=pt_courses_find_all");

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
                            c.getPart(),
                            trainer != null ? trainer.trainerName() : null,
                            org != null ? org.organizationId() : null,
                            org != null ? org.businessName() : null,
                            org != null ? org.roadAddress() : null,
                            org != null ? org.latitude() : null,
                            org != null ? org.longitude() : null,
                            trainer != null ? trainer.averageRating() : null,
                            trainer != null ? trainer.reviewCount() : 0,
                            c.getCreatedAt()
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

        // 강습 ID 목록으로 수강생 수를 한 번에 집계 (N+1 방지, 단일 쿼리)
        List<Long> courseIds = courses.stream().map(PtCourse::getId).toList();
        PtReservationCountQueryPort.StudentCounts counts =
                ptReservationCountQueryPort.countStudentsByPtCourseIds(courseIds);

        List<MyPtCourseListView> result = courses.stream()
                .map(course -> new MyPtCourseListView(
                        course.getId(),
                        resolveThumbnailUrl(course.getThumbnailFileId()),
                        course.getTitle(),
                        trainerName,
                        course.getStatus(),
                        counts.active().getOrDefault(course.getId(), 0),
                        counts.total().getOrDefault(course.getId(), 0)
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
        List<Long> userIds = reservations.stream().map(PtReservation::getUserId).distinct().toList();
        Map<Long, String> nicknameMap = userNicknameQueryPort.findNicknamesByUserIds(userIds);

        // 수강생 1명당 1줄 — 세션별 row를 userId 기준으로 집계
        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<PtReservation>> byUser = reservations.stream()
                .collect(Collectors.groupingBy(PtReservation::getUserId, LinkedHashMap::new, Collectors.toList()));

        List<CourseReservationView> reservationViews = byUser.entrySet().stream()
                .map(entry -> {
                    Long studentUserId = entry.getKey();
                    List<PtReservation> studentSessions = entry.getValue();
                    PtReservation rep = studentSessions.get(0);

                    // sessionStatus=COMPLETED(예약 종료 시각이 지난 회차)인 것 중 가장 최근 reservedEndAt
                    LocalDate lastPtDate = studentSessions.stream()
                            .filter(r -> r.getStatus() != PtReservationStatus.CANCELLED)
                            .filter(r -> r.getStatus() == PtReservationStatus.COMPLETED
                                    || r.getReservedEndAt().isBefore(now))
                            .map(r -> r.getReservedEndAt().toLocalDate())
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    int progressCount = ptReservationRepository.countProgressByUserIdAndPtCourseId(
                            studentUserId, rep.getPtCourseId());
                    int totalSessionCount = rep.getTotalSessionCount();

                    boolean allCancelled = studentSessions.stream()
                            .allMatch(r -> r.getStatus() == PtReservationStatus.CANCELLED);

                    PtReservationStatus derivedStatus;
                    if (allCancelled) {
                        derivedStatus = PtReservationStatus.CANCELLED;
                    } else if (progressCount == 0) {
                        derivedStatus = PtReservationStatus.RESERVED;
                    } else if (progressCount >= totalSessionCount) {
                        derivedStatus = PtReservationStatus.COMPLETED;
                    } else {
                        derivedStatus = PtReservationStatus.IN_PROGRESS;
                    }

                    return new CourseReservationView(
                            rep.getId(),
                            nicknameMap.getOrDefault(studentUserId, null),
                            derivedStatus,
                            lastPtDate,
                            progressCount,
                            totalSessionCount
                    );
                })
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

        int progressCount = ptReservationRepository.countProgressByUserIdAndPtCourseId(
                reservation.getUserId(), reservation.getPtCourseId());

        return new ReservationDetailView(
                studentProfile.nickname(),
                studentProfile.email(),
                studentProfile.phone(),
                reservation.getStatus(),
                progressCount,
                reservation.getTotalSessionCount(),
                ptCourse.getTitle()
        );
    }

    // PT 통계 조회
    @Cacheable(
            cacheNames = "ptMainStats",
            key = "'main'",
            sync = true
    )
    @Monitored(name = "gymjjak.pt.course.query.duration", domain = "pt_course", action = "find_stats")
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
    @Cacheable(
            cacheNames = "ptMainPopular",
            key = "'limit:4'",
            sync = true
    )
    @Monitored(name = "gymjjak.pt.course.query.duration", domain = "pt_course", action = "find_popular")
    @Override
    public List<PopularCourseView> findPopular() {
        log.debug("event=pt_courses_popular_find");

        // 예약 수 기준 인기 PT 강습을 먼저 조회한다.
        List<PtCourse> popularCourses = ptCourseRepository.findPopular(4);
        if (popularCourses.isEmpty()) {
            log.info("event=pt_courses_popular_find_succeeded count=0");
            return List.of();
        }

        // 인기 PT 목록에 포함된 조직 ID만 추출해서 조직 단건 조회 반복을 방지한다.
        List<Long> organizationIds = popularCourses.stream()
                .map(PtCourse::getOrganizationId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 인기 PT 목록에 포함된 트레이너 프로필 ID만 추출해서 트레이너 단건 조회 반복을 방지한다.
        List<Long> trainerProfileIds = popularCourses.stream()
                .map(PtCourse::getTrainerProfileId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 조직/트레이너 정보를 배치 조회하여 N+1 조회를 줄인다.
        Map<Long, OrganizationQueryPort.OrganizationInfo> organizationMap =
                organizationQueryPort.findAllByIds(organizationIds);

        Map<Long, TrainerSummaryInfo> trainerMap =
                trainerProfileQueryPort.findSummaryAllByIds(trainerProfileIds);

        // 조회된 인기 PT 목록과 배치 조회 결과를 조합해 응답 View를 생성한다.
        List<PopularCourseView> result = popularCourses.stream()
                .map(ptCourse -> {
                    OrganizationQueryPort.OrganizationInfo organization =
                            organizationMap.get(ptCourse.getOrganizationId());

                    TrainerSummaryInfo trainer =
                            trainerMap.get(ptCourse.getTrainerProfileId());

                    if (organization == null) {
                        log.warn(
                                "event=pt_course_popular_organization_not_found organizationId={}",
                                ptCourse.getOrganizationId()
                        );
                    }

                    if (trainer == null) {
                        log.warn(
                                "event=pt_course_popular_trainer_not_found trainerProfileId={}",
                                ptCourse.getTrainerProfileId()
                        );
                    }

                    return new PopularCourseView(
                            ptCourse.getId(),
                            ptCourse.getTitle(),
                            ptCourse.getPrice(),
                            resolveThumbnailUrl(ptCourse.getThumbnailFileId()),
                            ptCourse.getPart(),
                            trainer != null ? trainer.trainerName() : null,
                            organization != null ? organization.roadAddress() : null
                    );
                })
                .toList();

        log.info("event=pt_courses_popular_find_succeeded count={}", result.size());
        return result;
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
