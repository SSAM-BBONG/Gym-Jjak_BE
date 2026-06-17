package com.ssambbong.gymjjak.pt.feedback.application.service;

import com.ssambbong.gymjjak.pt.feedback.application.port.PtCurriculumQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackQueryUseCase;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackForbiddenException;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedbackQueryService implements FeedbackQueryUseCase {

    private final FeedbackRepository feedbackRepository;
    private final PtReservationQueryPort ptReservationQueryPort;
    private final PtCurriculumQueryPort ptCurriculumQueryPort;
    private final TrainerQueryPort trainerQueryPort;

    @Override
    public List<FeedbackListView> findFeedbacksByReservation(Long userId, Long ptReservationId) {
        log.debug("[FeedbackList] userId={}, ptReservationId={}", userId, ptReservationId);

        // 1. 예약 조회
        PtReservationQueryPort.ReservationInfo reservation =
                ptReservationQueryPort.findById(ptReservationId);

        // 2. 소유권 검증
        verifyOwnership(userId, reservation);

        // 3. 커리큘럼 목록 조회
        List<PtCurriculumQueryPort.CurriculumSummary> curricula =
                ptCurriculumQueryPort.findAllByPtCourseId(reservation.ptCourseId());

        // 4. ptCurriculumId 기준으로 피드백 목록을 Map
        Map<Long, Feedback> feedbackMap =
                feedbackRepository.findAllByPtReservationId(ptReservationId)
                        .stream()
                        .collect(Collectors.toMap(
                                Feedback::getPtCurriculumId,
                                f -> f,
                                (a, b) -> a   // 중복 시 먼저 저장된 거 유지
                        ));

        // 5. 커리큘럼별 피드백 매핑
        List<FeedbackListView> result = curricula.stream()
                .map(c -> toListView(c, feedbackMap.get(c.ptCurriculumId())))
                .toList();

        log.info("[FeedbackList] ptReservationId={} 조회 완료, 커리큘럼 수={}", ptReservationId, result.size());

        return result;
    }

    // USER: 본인 예약인지, TRAINER: 본인 강습인지 검증
    private void verifyOwnership(Long userId, PtReservationQueryPort.ReservationInfo reservation) {

        if (reservation.userId().equals(userId)) {
            return;
        }
        Long trainerProfileId = trainerQueryPort.findTrainerProfileIdByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("[FeedbackList] 접근 권한 없음 userId={}", userId);
                    return new FeedbackForbiddenException();
                });

        if (!trainerProfileId.equals(reservation.trainerProfileId())) {
            log.warn("[FeedbackList] 본인 강습 아님 userId={}, trainerProfileId={}", userId, trainerProfileId);
            throw new FeedbackForbiddenException();
        }
    }

    // 커리큘럼 + 피드백(nullable) → FeedbackListView 변환
    private FeedbackListView toListView(PtCurriculumQueryPort.CurriculumSummary curriculum,
                                        Feedback feedback) {
        FeedbackSummary summary = (feedback == null) ? null : new FeedbackSummary(
                feedback.getId(),
                feedback.getContent(),
                feedback.getCreatedAt().toLocalDate()
        );
        return new FeedbackListView(
                curriculum.ptCurriculumId(),
                curriculum.sessionNo(),
                curriculum.title(),
                summary
        );
    }

}
