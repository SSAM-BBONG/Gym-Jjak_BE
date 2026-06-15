package com.ssambbong.gymjjak.pt.ptReservation.application.service;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.PtCourseEnrichQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.FeedbackQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.PtCourseQueryPort;
import com.ssambbong.gymjjak.pt.ptReservation.application.usecase.PtReservationQueryUseCase;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.ptReservation.domain.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PtReservationQueryService implements PtReservationQueryUseCase {

    private final PtReservationRepository ptReservationRepository;
    private final PtCourseQueryPort ptCourseQueryPort; // title, thumbnailFileId
    private final FeedbackQueryPort feedbackQueryPort; // lastDate
    // TODO: PtCourseEnrichQueryPort 분리하면 바꾸기
    private final PtCourseEnrichQueryPort ptCourseEnrichQueryPort;

    @Override
    public List<MyPtReservationView> findMyReservations(Long userId, PtReservationStatus status) {
        log.debug("[MyPtReservations] userId={}, status={}", userId, status);

        List<PtReservation> reservations = ptReservationRepository.findAllByUserId(userId, status);

        List<MyPtReservationView> result = reservations.stream()
                .map(this::toView)
                .toList();

        log.info("[MyPtReservations] userId={}, 조회된 예약 수={}", userId, result.size());
        return result;
    }

    private MyPtReservationView toView(PtReservation reservation) {

        // pt_courses에서 title, thumbnailFileId 조회
        PtCourseQueryPort.PtCourseInfo courseInfo =
                ptCourseQueryPort.findPtCourseInfo(reservation.getPtCourseId());

        // trainer_profiles에서 트레이너 이름 조회
        String trainerName = ptCourseEnrichQueryPort
                .findTrainerProfileById(reservation.getTrainerProfileId())
                .trainerName();

        // feedbacks에서 가장 최근 피드백 날짜 조회
        LocalDate lastPtDate = feedbackQueryPort.findLastFeedbackDate(reservation.getId());

        // 위 + PtReservation 자체 데이터 합쳐 View 생성
        return new MyPtReservationView(
                reservation.getId(),            // ptReservationId
                courseInfo.thumbnailFileId(),   // pt_courses
                courseInfo.title(),             // pt_courses
                trainerName,                    // trainer_profiles
                reservation.getStatus(),        // pt_reservations 자기 컬럼
                lastPtDate,                      // feedbacks (현재 null)
                reservation.getProgressCount(), // pt_reservations 자기 컬럼
                reservation.getTotalSessionCount() // pt_reservations 자기 컬럼
        );
    }
}
