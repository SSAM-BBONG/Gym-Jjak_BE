package com.ssambbong.gymjjak.pt.ptReservation.application.service;

import com.ssambbong.gymjjak.file.application.result.FileUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.FeedbackQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.PtCourseQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.result.MonthlyPtReservationResult;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationForbiddenException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.exception.PtReservationNotFoundException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtSessionStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PtReservationQueryService implements PtReservationQueryUseCase {

    private final PtReservationRepository ptReservationRepository;
    private final PtCourseQueryPort ptCourseQueryPort; // title, thumbnailFileId, trainerName
    private final FeedbackQueryPort feedbackQueryPort; // lastDate
    private final FileUrlUseCase fileUrlUseCase;
    private final Clock clock;
    private static final int MONTH_RANGE = 6; // adminDashboard 월별 통계 기준 6개월

    @Override
    public List<MyPtReservationView> findMyReservations(Long userId, PtReservationStatus status) {
        log.debug("event=pt_reservation_list userId={}, status={}", userId, status);

        // 전체 세션 조회 후 코스별로 집계 (1코스 1카드)
        List<PtReservation> reservations = ptReservationRepository.findAllByUserId(userId, null);

        // ptCourseId 기준으로 집계, 최신 예약순 유지 (LinkedHashMap)
        Map<Long, List<PtReservation>> byCourse = reservations.stream()
                .collect(Collectors.groupingBy(PtReservation::getPtCourseId, LinkedHashMap::new, Collectors.toList()));

        List<MyPtReservationView> result = byCourse.values().stream()
                .map(sessions -> toView(userId, sessions))
                .filter(view -> status == null || view.status() == status)
                .toList();

        log.info("event=pt_reservation_list_succeeded userId={}, count={}", userId, result.size());
        return result;
    }

    @Override
    public PtReservationDetailView findMyReservationDetail(Long userId, Long ptReservationId) {
        log.debug("event=pt_reservation_detail userId={}, ptReservationId={}", userId, ptReservationId);

        PtReservation reservation = ptReservationRepository.findById(ptReservationId)
                .orElseThrow(() -> {
                    log.warn("event=pt_reservation_detail_failed reason=not_found, ptReservationId={}", ptReservationId);
                    return new PtReservationNotFoundException();
                });

        if (!reservation.getUserId().equals(userId)) {
            log.warn("event=pt_reservation_detail_failed reason=forbidden, userId={}, ownerId={}",
                    userId, reservation.getUserId());
            throw new PtReservationForbiddenException();
        }

        // pt_courses에서 title, thumbnailFileId, trainerName 조회
        PtCourseQueryPort.PtCourseInfo courseInfo =
                ptCourseQueryPort.findPtCourseInfo(reservation.getPtCourseId());

        // 커리큘럼 목록 조회
        List<PtCourseQueryPort.CurriculumInfo> curriculums =
                ptCourseQueryPort.findCurriculumsByPtCourseId(reservation.getPtCourseId());

        // 코스 전체 세션 예약 ID 조회 후 커리큘럼ID → 피드백ID 맵 구성
        List<Long> reservationIds = ptReservationRepository.findAllByUserId(userId, null)
                .stream()
                .filter(r -> r.getPtCourseId().equals(reservation.getPtCourseId()))
                .map(PtReservation::getId)
                .toList();
        Map<Long, Long> feedbackIdMap =
                feedbackQueryPort.findFeedbackIdMapByReservationIds(reservationIds);

        // 커리큘럼 + 피드백ID(없으면 null) 합쳐서 View 리스트 생성
        List<CurriculumView> curriculumViews = curriculums.stream()
                .map(c -> new CurriculumView(c.id(), c.sessionNo(), c.title(), feedbackIdMap.get(c.id())))
                .toList();

        int progressCount = ptReservationRepository.countProgressByUserIdAndPtCourseId(
                reservation.getUserId(), reservation.getPtCourseId());
        int totalSessionCount = reservation.getTotalSessionCount();

        List<PtReservation> mySessions = ptReservationRepository.findAllByUserId(userId, null)
                .stream()
                .filter(r -> r.getPtCourseId().equals(reservation.getPtCourseId()))
                .toList();

        boolean allCancelled = mySessions.stream()
                .allMatch(r -> r.getStatus() == PtReservationStatus.CANCELLED);
        boolean allCompleted = mySessions.stream()
                .allMatch(r -> r.getStatus() == PtReservationStatus.COMPLETED);

        PtReservationStatus derivedStatus;
        if (allCancelled) {
            derivedStatus = PtReservationStatus.CANCELLED;
        } else if (allCompleted || progressCount >= totalSessionCount) {
            derivedStatus = PtReservationStatus.COMPLETED;
        } else if (progressCount == 0) {
            derivedStatus = PtReservationStatus.RESERVED;
        } else {
            derivedStatus = PtReservationStatus.IN_PROGRESS;
        }

        log.info("event=pt_reservation_detail_succeeded ptReservationId={}", ptReservationId);
        return new PtReservationDetailView(
                reservation.getPtCourseId(),
                resolveThumbnailUrl(courseInfo.thumbnailFileId()),
                courseInfo.title(),
                courseInfo.trainerName(),
                derivedStatus,
                progressCount,
                totalSessionCount,
                reservation.getReservedStartAt(),
                curriculumViews
        );

    }

    @Override
    public int countProgressByUserIdAndPtCourseId(Long userId, Long ptCourseId) {
        return ptReservationRepository.countProgressByUserIdAndPtCourseId(userId, ptCourseId);
    }

    @Override
    public PtReservationStatus deriveCourseStatus(Long userId, Long ptCourseId) {
        List<PtReservation> sessions = ptReservationRepository.findAllByUserId(userId, null)
                .stream()
                .filter(r -> r.getPtCourseId().equals(ptCourseId))
                .toList();

        boolean allCancelled = sessions.stream().allMatch(r -> r.getStatus() == PtReservationStatus.CANCELLED);
        boolean allCompleted = sessions.stream().allMatch(r -> r.getStatus() == PtReservationStatus.COMPLETED);

        int progressCount = ptReservationRepository.countProgressByUserIdAndPtCourseId(userId, ptCourseId);
        int totalSessionCount = sessions.isEmpty() ? 0 : sessions.get(0).getTotalSessionCount();

        if (allCancelled) return PtReservationStatus.CANCELLED;
        if (allCompleted || progressCount >= totalSessionCount) return PtReservationStatus.COMPLETED;
        if (progressCount == 0) return PtReservationStatus.RESERVED;
        return PtReservationStatus.IN_PROGRESS;
    }

    // 내 PT 세션 목록 조회
    @Override
    public List<PtSessionView> findMySessions(Long userId) {
        log.debug("event=pt_session_list userId={}", userId);

        List<PtReservation> sessions = ptReservationRepository.findAllByUserId(userId, null);

        // distinct ptCourseId별 코스 정보 1회씩만 조회
        List<Long> distinctCourseIds = sessions.stream()
                .map(PtReservation::getPtCourseId)
                .distinct()
                .toList();

        Map<Long, PtCourseQueryPort.PtCourseInfo> courseInfoMap = distinctCourseIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        ptCourseQueryPort::findPtCourseInfo
                ));

        LocalDateTime now = LocalDateTime.now(clock);

        List<PtSessionView> result = sessions.stream()
                .map(r -> {
                    PtCourseQueryPort.PtCourseInfo info = courseInfoMap.get(r.getPtCourseId());
                    return new PtSessionView(
                            r.getId(),
                            r.getPtCourseId(),
                            info.title(),
                            info.trainerName(),
                            r.getReservedStartAt(),
                            r.getReservedEndAt(),
                            computeSessionStatus(r, now)
                    );
                })
                .toList();

        log.info("event=pt_session_list_succeeded userId={} count={}", userId, result.size());
        return result;
    }

    private PtSessionStatus computeSessionStatus(PtReservation r, LocalDateTime now) {
        if (r.getStatus() == PtReservationStatus.CANCELLED) return PtSessionStatus.CANCELLED;
        if (r.getStatus() == PtReservationStatus.COMPLETED || r.getReservedEndAt().isBefore(now)) {
            return PtSessionStatus.COMPLETED;
        }
        return PtSessionStatus.RESERVED;
    }

    // AdminDashboard : 월별 예약된 pt 수 조회
    @Override
    public List<MonthlyPtReservationResult> findMonthlyPtReservations() {
        log.info("event=pt_reservation_monthly_statistics_started");

        YearMonth currentMonth = YearMonth.now(clock);
        YearMonth startMonth = currentMonth.minusMonths(MONTH_RANGE - 1);

        // 시작 달 : 02
        LocalDateTime startDate = startMonth.atDay(1).atStartOfDay();
        // 종료 달
        LocalDateTime endDate = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
        // 월별 예약 수를 Map으로 반환
        Map<String, Long> reservationCountByMonth =
                ptReservationRepository.findMonthlyReservationCounts(
                                PtReservationStatus.CANCELLED,
                                startDate,
                                endDate
                        )
                        .stream()
                        // MonthlyReservationCount(월,인원) -> map 방식으로 변환해주는거 "월" : 인원
                        .collect(Collectors.toMap(
                                // key
                                PtReservationRepository.MonthlyReservationCount::month,
                                // value
                                PtReservationRepository.MonthlyReservationCount::count,
                                // month 값 중복이면 합산
                                Long::sum
                        ));

        List<MonthlyPtReservationResult> result =
                // 숫자 스트림 생성
                IntStream.range(0, MONTH_RANGE)
                        // int를 YearMonth 객체로 변환
                        .mapToObj(startMonth::plusMonths)
                        .map(month -> {
                            // YearMonth을 key로 저장, 아래 요건 그 "2026-06" 형태로 나옴
                            String monthKey = month.toString();

                            return MonthlyPtReservationResult.builder()
                                    .month(monthKey)
                                    // 있으면 값 사용, 없으면 0
                                    .count(reservationCountByMonth.getOrDefault(monthKey, 0L))
                                    .build();
                        })
                        .toList();

        log.info(
                "event=pt_reservation_monthly_statistics_succeeded, count={}",
                result.size()
        );

        return result;
    }

    private String resolveThumbnailUrl(Long fileId) {
        if (fileId == null) return null;
        try {
            FileUrlResult file = fileUrlUseCase.getUrl(fileId, null, false);
            return file.url();
        } catch (FileNotFoundException e) {
            log.warn("event=pt_reservation_thumbnail_not_found fileId={}", fileId);
            return null;
        } catch (RuntimeException e) {
            log.error("event=pt_reservation_thumbnail_url_resolve_failed fileId={}", fileId, e);
            return null;
        }
    }

    private MyPtReservationView toView(Long userId, List<PtReservation> sessions) {
        // CANCELLED 제외한 세션 중 대표 선택, 전부 취소됐으면 첫 번째 사용
        PtReservation rep = sessions.stream()
                .filter(r -> r.getStatus() != PtReservationStatus.CANCELLED)
                .findFirst()
                .orElse(sessions.get(0));

        PtCourseQueryPort.PtCourseInfo courseInfo =
                ptCourseQueryPort.findPtCourseInfo(rep.getPtCourseId());

        // sessionStatus=COMPLETED(예약 종료 시각이 지난 회차)인 것 중 가장 최근 reservedEndAt
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate lastPtDate = sessions.stream()
                .filter(r -> r.getStatus() != PtReservationStatus.CANCELLED)
                .filter(r -> r.getStatus() == PtReservationStatus.COMPLETED
                        || r.getReservedEndAt().isBefore(now))
                .map(r -> r.getReservedEndAt().toLocalDate())
                .max(Comparator.naturalOrder())
                .orElse(null);

        int progressCount = ptReservationRepository.countProgressByUserIdAndPtCourseId(
                userId, rep.getPtCourseId());
        int totalSessionCount = rep.getTotalSessionCount();

        boolean allCancelled = sessions.stream()
                .allMatch(r -> r.getStatus() == PtReservationStatus.CANCELLED);
        boolean allCompleted = sessions.stream()
                .allMatch(r -> r.getStatus() == PtReservationStatus.COMPLETED);

        PtReservationStatus derivedStatus;
        if (allCancelled) {
            derivedStatus = PtReservationStatus.CANCELLED;
        } else if (allCompleted || progressCount >= totalSessionCount) {
            derivedStatus = PtReservationStatus.COMPLETED;
        } else if (progressCount == 0) {
            derivedStatus = PtReservationStatus.RESERVED;
        } else {
            derivedStatus = PtReservationStatus.IN_PROGRESS;
        }

        return new MyPtReservationView(
                rep.getId(),
                resolveThumbnailUrl(courseInfo.thumbnailFileId()),
                courseInfo.title(),
                courseInfo.trainerName(),
                derivedStatus,
                lastPtDate,
                progressCount,
                totalSessionCount
        );
    }
}
