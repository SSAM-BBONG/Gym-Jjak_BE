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
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Map;
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

        List<PtReservation> reservations = ptReservationRepository.findAllByUserId(userId, status);

        List<MyPtReservationView> result = reservations.stream()
                .map(this::toView)
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

        // 커리큘럼ID → 피드백ID 맵 조회
        Map<Long, Long> feedbackIdMap =
                feedbackQueryPort.findFeedbackIdMapByReservationId(ptReservationId);

        // 커리큘럼 + 피드백ID(없으면 null) 합쳐서 View 리스트 생성
        List<CurriculumView> curriculumViews = curriculums.stream()
                .map(c -> new CurriculumView(c.id(), c.sessionNo(), c.title(), feedbackIdMap.get(c.id())))
                .toList();

        int progressCount = ptReservationRepository.countCompletedByUserIdAndPtCourseId(
                reservation.getUserId(), reservation.getPtCourseId());

        log.info("event=pt_reservation_detail_succeeded ptReservationId={}", ptReservationId);
        return new PtReservationDetailView(
                resolveThumbnailUrl(courseInfo.thumbnailFileId()),
                courseInfo.title(),
                courseInfo.trainerName(),
                reservation.getStatus(),
                progressCount,
                reservation.getTotalSessionCount(),
                curriculumViews
        );

    }

    @Override
    public int countProgressByUserIdAndPtCourseId(Long userId, Long ptCourseId) {
        return ptReservationRepository.countCompletedByUserIdAndPtCourseId(userId, ptCourseId);
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

    private MyPtReservationView toView(PtReservation reservation) {

        // pt_courses에서 title, thumbnailFileId, trainerName 조회
        PtCourseQueryPort.PtCourseInfo courseInfo =
                ptCourseQueryPort.findPtCourseInfo(reservation.getPtCourseId());

        // feedbacks에서 가장 최근 피드백 날짜 조회
        LocalDate lastPtDate = feedbackQueryPort.findLastFeedbackDate(reservation.getId());

        int progressCount = ptReservationRepository.countCompletedByUserIdAndPtCourseId(
                reservation.getUserId(), reservation.getPtCourseId());

        // 위 + PtReservation 자체 데이터 합쳐 View 생성
        return new MyPtReservationView(
                reservation.getId(),
                resolveThumbnailUrl(courseInfo.thumbnailFileId()),
                courseInfo.title(),
                courseInfo.trainerName(),
                reservation.getStatus(),
                lastPtDate,
                progressCount,
                reservation.getTotalSessionCount()
        );
    }
}
