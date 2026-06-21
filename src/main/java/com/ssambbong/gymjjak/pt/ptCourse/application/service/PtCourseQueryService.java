package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.category.application.usecase.CategoryQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.*;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseQueryUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseForbiddenException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseStatusInvalidException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.TrainerProfileNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseScheduleRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    @Override
    public List<PtCourseListView> findAllPtCourses() {
        log.debug("event=pt_courses_find_all");

        Map<Long, String> categoryMap = buildCategoryMap();
        List<PtCourseListView> result = ptCourseRepository.findAllVisible().stream()
                .map(ptCourse -> toListView(ptCourse, categoryMap))
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
            throw new PtCourseNotFoundException();
        }

        log.info("event=pt_course_detail_find_succeeded ptCourseId={}", ptCourseId);
        return toDetailView(ptCourse);
    }

    @Override
    public List<MyPtCourseListView> findMyPtCourses(Long userId, PtCourseStatus status) {
        log.debug("event=pt_my_courses_find userId={}, status={}", userId, status);

        // 전체(null)/활성화(VISIBLE)/비활성화(HIDDEN)만 허용
        if (status == PtCourseStatus.BLOCKED || status == PtCourseStatus.DELETED) {
            throw new PtCourseStatusInvalidException();
        }

        // 로그인한 userId로 트레이너 프로필 ID 조회
        TrainerProfileQueryPort.TrainerInfo trainerInfo;
        try {
            trainerInfo = trainerProfileQueryPort.findByUserId(userId);
        } catch (TrainerProfileNotFoundException e) {
            log.warn("event=pt_my_courses_find_failed reason=trainer_not_found, userId={}", userId);
            throw e;
        }

        // 같은 트레이너의 강습이므로 trainerName은 1회만 조회 후 전체 카드에 재사용
        String trainerName = trainerProfileQueryPort
                .findSummaryById(trainerInfo.trainerProfileId())
                .trainerName();

        // status=null이면 VISIBLE+HIDDEN 전체, 지정 시 해당 status만
        List<PtCourse> courses = ptCourseRepository
                .findAllByTrainerProfileId(trainerInfo.trainerProfileId(), status);

        // 강습 ID 목록으로 예약 수를 한 번에 집계 (N+1 방지)
        List<Long> courseIds = courses.stream().map(PtCourse::getId).toList();
        Map<Long, Integer> activeCounts = ptReservationCountQueryPort.countActiveByPtCourseIds(courseIds);
        Map<Long, Integer> totalCounts = ptReservationCountQueryPort.countTotalByPtCourseIds(courseIds);

        List<MyPtCourseListView> result = courses.stream()
                .map(course -> new MyPtCourseListView(
                        course.getId(),
                        course.getThumbnailFileId(),
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
        TrainerProfileQueryPort.TrainerInfo trainerInfo;
        try {
            trainerInfo = trainerProfileQueryPort.findByUserId(userId);
        } catch (TrainerProfileNotFoundException e) {
            log.warn("event=pt_course_reservations_find_failed reason=trainer_not_found userId={}", userId);
            throw e;
        }
        if (!ptCourse.getTrainerProfileId().equals(trainerInfo.trainerProfileId())) {
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

    // categoryId -> categoryName 매핑
    private Map<Long, String> buildCategoryMap() {
        return categoryQueryUseCase.handle().stream()
                .collect(Collectors.toMap(
                        CategoryQueryUseCase.CategoryView::categoryId,
                        CategoryQueryUseCase.CategoryView::name
                ));
    }

    // ptCourse + enrich(조직/트레이너) -> 목록 응답용 View 변환
    private PtCourseListView toListView(PtCourse ptCourse, Map<Long, String> categoryMap) {
        OrganizationQueryPort.OrganizationInfo org =
                organizationQueryPort.findById(ptCourse.getOrganizationId());
        TrainerProfileQueryPort.TrainerDisplayInfo trainer =
                trainerProfileQueryPort.findById(ptCourse.getTrainerProfileId());

        return new PtCourseListView(
                ptCourse.getId(),
                ptCourse.getTitle(),
                ptCourse.getThumbnailFileId(),
                ptCourse.getPrice(),
                ptCourse.getTagId(),
                null, // TODO: TagQueryUseCase 연동 후 tagName 채우기
                ptCourse.getCategoryId(),
                categoryMap.getOrDefault(ptCourse.getCategoryId(), null),
                trainer.trainerName(),
                org.organizationId(),
                org.businessName(),
                org.roadAddress(),
                org.latitude(),
                org.longitude(),
                trainer.reviewCount()
        );
    }

    // ptCourse + TrainerDisplayInfo + 커리큘럼/스케쥴 목록 -> 상세 응답용 View 반환
    private PtCourseDetailView toDetailView(PtCourse ptCourse) {
        TrainerProfileQueryPort.TrainerDisplayInfo trainer =
                trainerProfileQueryPort.findById(ptCourse.getTrainerProfileId());

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
                ptCourse.getThumbnailFileId(),
                ptCourse.getTitle(),
                ptCourse.getDescription(),
                ptCourse.getPrice(),
                ptCourse.getTotalSessionCount(),
                trainer.averageRating(),
                trainer.reviewCount(),
                ptCourse.getOrganizationId(),
                ptCourse.getTrainerProfileId(),
                trainer.trainerName(),
                trainer.profileFileId(),
                trainer.introduction(),
                trainer.certifications(),
                trainer.awards(),
                curriculums,
                schedules,
                // 미구현
                List.of()
        );
    }
}
