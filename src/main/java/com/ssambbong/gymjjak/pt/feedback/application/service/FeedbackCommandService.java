package com.ssambbong.gymjjak.pt.feedback.application.service;

import com.ssambbong.gymjjak.pt.feedback.application.command.CreateFeedbackCommand;
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
    public Long createFeedback(CreateFeedbackCommand command) {
        log.debug("[FeedbackCreate] userId={}, ptReservationId={} 피드백 등록 시작",
                command.userId(), command.ptReservationId());

        // 예약 조회
        PtReservationQueryPort.ReservationInfo reservation =
                ptReservationQueryPort.findById(command.ptReservationId());

        // 트레이너 본인 예약인지 확인
        Long trainerProfileId = trainerQueryPort.findTrainerProfileIdByUserId(command.userId())
                .orElseThrow(FeedbackForbiddenException::new);

        if (!reservation.trainerProfileId().equals(trainerProfileId)) {
            throw new FeedbackForbiddenException();
        }

        // 커리큘럼이 해당 코스 소속인지 확인
        ptCurriculumQueryPort.findByIdAndPtCourseId(command.ptCurriculumId(), reservation.ptCourseId());

        // 중복 피드백 확인
        if (feedbackRepository.existsByPtReservationIdAndPtCurriculumId(
                command.ptReservationId(), command.ptCurriculumId())) {
            throw new FeedbackAlreadyExistsException();
        }

        // 피드백 저장
        Feedback saved = feedbackRepository.save(
                Feedback.create(command.ptReservationId(), command.ptCurriculumId(),
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
