package com.ssambbong.gymjjak.pt.feedback.application.service;

import com.ssambbong.gymjjak.pt.feedback.application.port.PtCurriculumQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackQueryUseCase;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackForbiddenException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackNotFoundException;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackMediaRepository;
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
    private final FeedbackMediaRepository feedbackMediaRepository;
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

    @Override
    public FeedbackDetailView findFeedbackDetail(Long userId, Long ptReservationId, Long feedbackId) {
        log.debug("[FeedbackDetail] userId={}, ptReservationId={}, feedbackId={}", userId, ptReservationId, feedbackId);

        // 1. 피드백 조회
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> {
            log.warn("[FeedbackDetail] 피드백 없음 feedbackId={}", feedbackId);
            return new FeedbackNotFoundException();
        });

        // 2. path param의 ptReservationId와 피드백의 예약 ID 일치 확인
        if (!feedback.getPtReservationId().equals(ptReservationId)) {
            log.warn("[FeedbackDetail] 예약 불일치 feedbackId={}, ptReservationId={}", feedbackId, ptReservationId);
            throw new FeedbackNotFoundException();
        }

        // 3. 소유권 검증
        verifyOwnershipByFeedback(userId, feedback);

        // 4. 커리큘럼 조회 (sessionNo, title)
        PtCurriculumQueryPort.CurriculumSummary curriculum =
                ptCurriculumQueryPort.findById(feedback.getPtCurriculumId());

        // 5. 미디어 목록 조회
        List<MediaView> mediaList =
                feedbackMediaRepository.findAllByFeedbackId(feedbackId)
                        .stream()
                        .map(m -> new MediaView(m.getId(), m.getMediaType(), m.getFileId()))
                        .toList();

        log.info("[FeedbackDetail] feedbackId={} 조회 완료", feedbackId);

        return new FeedbackDetailView(
                curriculum.sessionNo(),
                curriculum.title(),
                feedback.getContent(),
                mediaList,
                feedback.getCreatedAt().toLocalDate()
        );

    }

    // 목록 조회용 -> USER: 본인 예약인지, TRAINER: 본인 강습인지 검증
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

    // 상세 조회용 소유권 검증
    private void verifyOwnershipByFeedback(Long userId, Feedback feedback) {

        if (feedback.getUserId().equals(userId)) return;

        Long trainerProfileId = trainerQueryPort.findTrainerProfileIdByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("[FeedbackDetail] 접근 권한 없음 userId={}", userId);
                    return new FeedbackForbiddenException();
                });

        if (!trainerProfileId.equals(feedback.getTrainerProfileId())) {
            log.warn("[FeedbackDetail] 본인 강습 아님 userId={}, trainerProfileId={}", userId, trainerProfileId);
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
