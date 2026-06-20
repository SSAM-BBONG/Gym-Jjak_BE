package com.ssambbong.gymjjak.pt.feedback.application.service;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.pt.feedback.application.command.CreateFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtCurriculumQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackCommandUseCase;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackAlreadyExistsException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackForbiddenException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackMediaInvalidException;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMedia;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackMediaRepository;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final FileUseCase fileUseCase;

    @Override
    public Long createFeedback(CreateFeedbackCommand command) {
        log.debug("event=feedback_create userId={} ptReservationId={} ptCurriculumId={}",
                command.userId(), command.ptReservationId(), command.ptCurriculumId());

        // 미디어 타입 중복 검증 (BEFORE/AFTER 각 1개)
        Set<FeedbackMediaType> mediaTypes = command.media().stream()
                .map(CreateFeedbackCommand.MediaCommand::mediaType)
                .collect(Collectors.toSet());
        if (mediaTypes.size() != command.media().size()) {
            throw new FeedbackMediaInvalidException();
        }

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

        // 미디어 파일 일괄 등록
        List<FileRegistrationResult> fileResults = registerMediaFiles(command.userId(), command.media());

        // 피드백 저장
        Feedback saved = feedbackRepository.save(
                Feedback.create(command.ptReservationId(), command.ptCurriculumId(),
                        trainerProfileId, reservation.userId(), command.content())
        );

        // 파일 등록 결과와 미디어 타입을 인덱스 기준으로 매핑하여 저장
        List<FeedbackMedia> mediaList = IntStream.range(0, command.media().size())
                .mapToObj(i -> FeedbackMedia.create(
                        saved.getId(),
                        command.media().get(i).mediaType(),
                        fileResults.get(i).fileId()
                ))
                .toList();
        feedbackMediaRepository.saveAll(mediaList);

        log.info("event=feedback_create_complete feedbackId={}", saved.getId());
        return saved.getId();
    }

    private List<FileRegistrationResult> registerMediaFiles(Long userId,
                                                             List<CreateFeedbackCommand.MediaCommand> media) {
        List<CreateFileCommand> fileCommands = media.stream()
                .map(m -> new CreateFileCommand(
                        userId,
                        m.file().fileKey(),
                        m.file().originalName(),
                        m.file().contentType(),
                        m.file().fileSize(),
                        FileType.FEEDBACK_VIDEO
                ))
                .toList();

        List<FileRegistrationResult> results = fileUseCase.registerFiles(fileCommands);

        if (results.size() != media.size()) {
            log.error("event=feedback_media_register_failed reason=unexpected_result userId={} expected={} actual={}",
                    userId, media.size(), results.size());
            throw new IllegalStateException("미디어 파일 등록 결과가 올바르지 않습니다.");
        }

        return results;
    }
}
