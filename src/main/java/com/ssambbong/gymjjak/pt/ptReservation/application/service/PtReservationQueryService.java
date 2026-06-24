package com.ssambbong.gymjjak.pt.ptReservation.application.service;

import com.ssambbong.gymjjak.pt.ptReservation.application.port.FeedbackQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.PtCourseQueryPort;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PtReservationQueryService implements PtReservationQueryUseCase {

    private final PtReservationRepository ptReservationRepository;
    private final PtCourseQueryPort ptCourseQueryPort; // title, thumbnailFileId, trainerName
    private final FeedbackQueryPort feedbackQueryPort; // lastDate

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

        log.info("event=pt_reservation_detail_succeeded ptReservationId={}", ptReservationId);
        return new PtReservationDetailView(
                courseInfo.thumbnailFileId(),
                courseInfo.title(),
                courseInfo.trainerName(),
                reservation.getStatus(),
                reservation.getProgressCount(),
                reservation.getTotalSessionCount(),
                curriculumViews
        );

    }

    private MyPtReservationView toView(PtReservation reservation) {

        // pt_courses에서 title, thumbnailFileId, trainerName 조회
        PtCourseQueryPort.PtCourseInfo courseInfo =
                ptCourseQueryPort.findPtCourseInfo(reservation.getPtCourseId());

        // feedbacks에서 가장 최근 피드백 날짜 조회
        LocalDate lastPtDate = feedbackQueryPort.findLastFeedbackDate(reservation.getId());

        // 위 + PtReservation 자체 데이터 합쳐 View 생성
        return new MyPtReservationView(
                reservation.getId(),              // ptReservationId
                courseInfo.thumbnailFileId(),     // pt_courses
                courseInfo.title(),               // pt_courses
                courseInfo.trainerName(),         // trainer_profiles (via adapter)
                reservation.getStatus(),        // pt_reservations 자기 컬럼
                lastPtDate,                      // feedbacks (현재 null)
                reservation.getProgressCount(), // pt_reservations 자기 컬럼
                reservation.getTotalSessionCount() // pt_reservations 자기 컬럼
        );
    }
}
