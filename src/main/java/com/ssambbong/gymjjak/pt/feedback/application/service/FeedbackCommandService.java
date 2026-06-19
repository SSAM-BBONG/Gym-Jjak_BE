package com.ssambbong.gymjjak.pt.feedback.application.service;

import com.ssambbong.gymjjak.pt.feedback.application.port.PtCurriculumQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackCommandUseCase;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackAlreadyExistsException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackForbiddenException;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMedia;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackMediaRepository;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackCommandService implements FeedbackCommandUseCase {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMediaRepository feedbackMediaRepository;
    private final PtReservationQueryPort ptReservationQueryPort;
    private final PtCurriculumQueryPort ptCurriculumQueryPort;
    private final TrainerQueryPort trainerQueryPort;

    @Override
    public Long createFeedback(Long userId, Long ptReservationId, CreateFeedbackCommand command) {
        log.debug("[FeedbackCreate] userId={}, ptReservationId={} 피드백 등록 시작", userId, ptReservationId);

        // 예약 조회
        PtReservationQueryPort.ReservationInfo reservation = ptReservationQueryPort.findById(ptReservationId);

        // 트레이너 본인 예약인지 확인
        Long trainerProfileId = trainerQueryPort.findTrainerProfileIdByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("[FeedbackCreate] userId={} 트레이너 프로필 없음", userId);
                    return new FeedbackForbiddenException();
                });

        if (!reservation.trainerProfileId().equals(trainerProfileId)) {
            log.warn("[FeedbackCreate] userId={} 본인 수강생 예약이 아님", userId);
            throw new FeedbackForbiddenException();
        }

        // 커리뮬럼이 해당 코스 소속인지 확인
        ptCurriculumQueryPort.findByIdAndPtCourseId(command.ptCurriculumId(), reservation.ptCourseId());

        // 중복 피드백 확인
        if (feedbackRepository.existsByPtReservationIdAndPtCurriculumId(ptReservationId, command.ptCurriculumId())) {
            log.warn("[FeedbackCreate] ptReservationId={}, ptCurriculumId={} 이미 피드백 존재",
                    ptReservationId, command.ptCurriculumId());
            throw new FeedbackAlreadyExistsException();
        }

        // 피드백 저장
        Feedback saved = feedbackRepository.save(
                Feedback.create(ptReservationId, command.ptCurriculumId(),
                        trainerProfileId, reservation.userId(), command.content())
        );

        // 미디어 저장
        List<FeedbackMedia> mediaList = command.media().stream()
                .map(m -> FeedbackMedia.create(saved.getId(), m.mediaType(), m.fileId()))
                .toList();
        feedbackMediaRepository.saveAll(mediaList);

        log.info("[FeedbackCreate] feedbackId={} 피드백 등록 완료", saved.getId());
        return saved.getId();
    }
}
