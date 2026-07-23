package com.ssambbong.gymjjak.pt.feedback.application.service;

import com.ssambbong.gymjjak.file.application.result.FileUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
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
import java.util.Optional;
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
    private final FileUrlUseCase fileUrlUseCase;

    @Override
    public List<FeedbackListView> findFeedbacksByReservation(Long userId, Long ptReservationId) {
        log.debug("event=feedback_list_query userId={} ptReservationId={}", userId, ptReservationId);

        // 1. 예약 조회
        PtReservationQueryPort.ReservationInfo reservation =
                ptReservationQueryPort.findById(ptReservationId);

        // 2. 소유권 검증
        verifyOwnership(userId, reservation);

        // 3. 커리큘럼 목록 조회
        List<PtCurriculumQueryPort.CurriculumSummary> curricula =
                ptCurriculumQueryPort.findAllByPtCourseId(reservation.ptCourseId());

        // 4. 해당 유저의 코스 전체 세션 예약 ID 조회 후 피드백 Map 구성
        List<Long> reservationIds = ptReservationQueryPort.findReservationIdsByUserIdAndPtCourseId(
                reservation.userId(), reservation.ptCourseId());
        Map<Long, Feedback> feedbackMap =
                feedbackRepository.findAllByPtReservationIds(reservationIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Feedback::getPtCurriculumId,
                                f -> f,
                                (a, b) -> a   // 중복 시 먼저 저장된 거 유지
                        ));

        // 5. 예약 ID → 예약 시작일 맵 조회 (피드백 회차별 날짜 표시용)
        Map<Long, java.time.LocalDate> reservationDateMap =
                ptReservationQueryPort.findReservationStartDatesByUserIdAndPtCourseId(
                        reservation.userId(), reservation.ptCourseId());

        // 6. 커리큘럼별 피드백 매핑
        List<FeedbackListView> result = curricula.stream()
                .map(c -> {
                    Feedback feedback = feedbackMap.get(c.ptCurriculumId());
                    java.time.LocalDate reservedStartAt = (feedback != null)
                            ? reservationDateMap.get(feedback.getPtReservationId())
                            : null;
                    return toListView(c, feedback, reservedStartAt);
                })
                .toList();

        log.info("event=feedback_list_query_complete ptReservationId={} curriculumCount={}", ptReservationId, result.size());

        return result;
    }

    @Override
    public FeedbackDetailView findFeedbackDetail(Long userId, Long ptReservationId, Long feedbackId) {
        log.debug("event=feedback_detail_query userId={} ptReservationId={} feedbackId={}", userId, ptReservationId, feedbackId);

        // 1. 피드백 조회
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(FeedbackNotFoundException::new);

        // 2. path param 예약과 피드백 예약이 같은 코스인지 확인
        PtReservationQueryPort.ReservationInfo pathReservation = ptReservationQueryPort.findById(ptReservationId);
        PtReservationQueryPort.ReservationInfo feedbackReservation = ptReservationQueryPort.findById(feedback.getPtReservationId());
        if (!pathReservation.ptCourseId().equals(feedbackReservation.ptCourseId())) {
            throw new FeedbackNotFoundException();
        }

        // 3. 소유권 검증 + isMine 계산 (트레이너 프로필 조회 1회)
        Optional<Long> trainerProfileId = trainerQueryPort.findTrainerProfileIdByUserId(userId);
        verifyOwnershipByFeedback(userId, feedback, trainerProfileId);

        // 4. 커리큘럼 조회 (sessionNo, title)
        PtCurriculumQueryPort.CurriculumSummary curriculum =
                ptCurriculumQueryPort.findById(feedback.getPtCurriculumId());

        // 5. 미디어 목록 조회 (FEEDBACK_VIDEO는 public → requesterId 없이 URL 변환)
        List<MediaView> mediaList =
                feedbackMediaRepository.findAllByFeedbackId(feedbackId)
                        .stream()
                        .map(m -> new MediaView(m.getId(), m.getMediaType(), resolveMediaUrl(m.getFileId(), userId)))
                        .toList();

        log.info("event=feedback_detail_query_complete feedbackId={}", feedbackId);

        boolean isMine = trainerProfileId
                .map(id -> id.equals(feedback.getTrainerProfileId()))
                .orElse(false);

        return new FeedbackDetailView(
                curriculum.sessionNo(),
                curriculum.title(),
                feedback.getContent(),
                mediaList,
                feedback.getCreatedAt().toLocalDate(),
                isMine
        );

    }

    // 목록 조회용 -> USER: 본인 예약인지, TRAINER: 본인 강습인지 검증
    private void verifyOwnership(Long userId, PtReservationQueryPort.ReservationInfo reservation) {

        if (reservation.userId().equals(userId)) {
            return;
        }
        Long trainerProfileId = trainerQueryPort.findTrainerProfileIdByUserId(userId)
                .orElseThrow(FeedbackForbiddenException::new);

        if (!trainerProfileId.equals(reservation.trainerProfileId())) {
            throw new FeedbackForbiddenException();
        }
    }

    // 상세 조회용 소유권 검증 (trainerProfileId는 호출부에서 조회해 전달)
    private void verifyOwnershipByFeedback(Long userId, Feedback feedback, Optional<Long> trainerProfileId) {

        if (feedback.getUserId().equals(userId)) return;

        Long profileId = trainerProfileId.orElseThrow(FeedbackForbiddenException::new);

        if (!profileId.equals(feedback.getTrainerProfileId())) {
            throw new FeedbackForbiddenException();
        }
    }

    // isAdmin=true로 소유권 검증 우회
    private String resolveMediaUrl(Long fileId, Long userId) {
        if (fileId == null) return null;
        try {
            FileUrlResult file = fileUrlUseCase.getUrl(fileId, userId, true);
            return file.url();
        } catch (FileNotFoundException e) {
            log.warn("event=feedback_media_file_not_found fileId={}", fileId);
            return null;
        } catch (RuntimeException e) {
            log.error("event=feedback_media_url_resolve_failed fileId={}", fileId, e);
            return null;
        }
    }

    // 커리큘럼 + 피드백(nullable) + 예약 시작일(nullable) → FeedbackListView 변환
    private FeedbackListView toListView(PtCurriculumQueryPort.CurriculumSummary curriculum,
                                        Feedback feedback,
                                        java.time.LocalDate reservedStartAt) {
        FeedbackSummary summary = (feedback == null) ? null : new FeedbackSummary(
                feedback.getId(),
                feedback.getContent(),
                feedback.getCreatedAt().toLocalDate()
        );
        return new FeedbackListView(
                curriculum.ptCurriculumId(),
                curriculum.sessionNo(),
                curriculum.title(),
                reservedStartAt,
                summary
        );
    }

}
