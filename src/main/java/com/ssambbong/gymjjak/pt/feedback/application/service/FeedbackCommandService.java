package com.ssambbong.gymjjak.pt.feedback.application.service;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.pt.feedback.application.command.CreateFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.command.DeleteFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.command.UpdateFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.pt.feedback.application.event.FeedbackCreatedEvent;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtCurriculumQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackCommandUseCase;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackAlreadyExistsException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackForbiddenException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackMediaInvalidException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackNotFoundException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackReservationCancelledException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackReservationCompletedException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackSessionNotCompletedException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackUpdateNotAllowedException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMedia;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackMediaRepository;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
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
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

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

        // 취소된 예약은 세션이 진행되지 않았으므로 피드백 작성 불가
        if (reservation.status() == PtReservationStatus.CANCELLED) {
            throw new FeedbackReservationCancelledException();
        }

        // sessionStatus 기준: DB status가 COMPLETED이거나 예약 종료 시각이 지난 경우만 피드백 작성 가능
        boolean sessionCompleted = reservation.status() == PtReservationStatus.COMPLETED
                || reservation.reservedEndAt().isBefore(LocalDateTime.now(clock));
        if (!sessionCompleted) {
            throw new FeedbackSessionNotCompletedException();
        }

        // 커리큘럼이 해당 코스 소속인지 확인
        ptCurriculumQueryPort.findByIdAndPtCourseId(command.ptCurriculumId(), reservation.ptCourseId());

        // 코스 전체 세션 기준 동일 커리큘럼 중복 피드백 확인
        List<Long> courseReservationIds = ptReservationQueryPort.findReservationIdsByUserIdAndPtCourseId(
                reservation.userId(), reservation.ptCourseId());
        if (feedbackRepository.existsByPtReservationIdsAndPtCurriculumId(
                courseReservationIds, command.ptCurriculumId())) {
            throw new FeedbackAlreadyExistsException();
        }

        // 미디어 파일 일괄 등록 전 필수값 검증
        for (CreateFeedbackCommand.MediaCommand m : command.media()) {
            if (m.file() == null || m.file().fileKey() == null || m.file().fileKey().isBlank()
                    || m.file().originalName() == null || m.file().originalName().isBlank()
                    || m.file().contentType() == null || m.file().contentType().isBlank()
                    || m.file().fileSize() == null || m.file().fileSize() <= 0) {
                throw new FeedbackMediaInvalidException();
            }
        }

        // 미디어 파일 일괄 등록
        List<FileRegistrationResult> fileResults = registerMediaFiles(command.userId(),
                command.media().stream().map(CreateFeedbackCommand.MediaCommand::file).toList());

        // 피드백 저장
        Feedback saved = feedbackRepository.save(
                Feedback.create(command.ptReservationId(), command.ptCurriculumId(),
                        trainerProfileId, reservation.userId(), command.content())
        );

        // registerFiles()는 입력 순서와 동일한 순서로 결과를 반환한다 (Stream.toList() 삽입 순서 보장).
        // 인덱스 기준으로 mediaType과 fileId를 매핑한다.
        List<FeedbackMedia> mediaList = IntStream.range(0, command.media().size())
                .mapToObj(i -> FeedbackMedia.create(
                        saved.getId(),
                        command.media().get(i).mediaType(),
                        fileResults.get(i).fileId()
                ))
                .toList();
        feedbackMediaRepository.saveAll(mediaList);

        // 피드백 등록 완료 - 예약 회원에게 알림 발행
        eventPublisher.publishEvent(new FeedbackCreatedEvent(
                reservation.userId(),
                saved.getId()
        ));

        log.info("event=feedback_create_complete feedbackId={}", saved.getId());
        return saved.getId();
    }

    @Override
    public Long updateFeedback(UpdateFeedbackCommand command) {
        log.debug("event=feedback_update userId={} feedbackId={}", command.userId(), command.feedbackId());

        // 미디어 타입 중복 검증 (BEFORE/AFTER 각 1개) — media null이면 기존 유지이므로 스킵
        if (command.media() != null) {
            Set<FeedbackMediaType> mediaTypes = command.media().stream()
                    .map(UpdateFeedbackCommand.MediaCommand::mediaType)
                    .collect(Collectors.toSet());
            if (mediaTypes.size() != command.media().size()) {
                throw new FeedbackMediaInvalidException();
            }
        }

        // 피드백 조회
        Feedback feedback = feedbackRepository.findById(command.feedbackId())
                .orElseThrow(() -> {
                    log.warn("event=feedback_update_failed reason=not_found feedbackId={}", command.feedbackId());
                    return new FeedbackNotFoundException();
                });

        // path param 예약과 피드백 예약이 같은 코스인지 확인
        PtReservationQueryPort.ReservationInfo pathReservation = ptReservationQueryPort.findById(command.ptReservationId());
        PtReservationQueryPort.ReservationInfo feedbackReservation = ptReservationQueryPort.findById(feedback.getPtReservationId());
        if (!pathReservation.ptCourseId().equals(feedbackReservation.ptCourseId())) {
            throw new FeedbackNotFoundException();
        }

        // 트레이너 본인 피드백인지 확인
        Long trainerProfileId = trainerQueryPort.findTrainerProfileIdByUserId(command.userId())
                .orElseThrow(FeedbackForbiddenException::new);

        if (!feedback.getTrainerProfileId().equals(trainerProfileId)) {
            log.warn("event=feedback_update_failed reason=forbidden userId={} feedbackId={}",
                    command.userId(), command.feedbackId());
            throw new FeedbackForbiddenException();
        }

        // 예약 전체가 완료된 경우 수정 불가 (세션 종료 시각은 체크하지 않음 — 피드백은 세션 후 작성)
        if (feedbackReservation.status() == PtReservationStatus.COMPLETED) {
            throw new FeedbackUpdateNotAllowedException();
        }

        // 피드백 내용 수정
        feedback.update(command.content());
        feedbackRepository.update(feedback);

        // media null이거나 빈 리스트면 기존 미디어 유지, 있으면 전체 교체
        if (command.media() != null && !command.media().isEmpty()) {
            for (UpdateFeedbackCommand.MediaCommand m : command.media()) {
                if (m.file() == null || m.file().fileKey() == null || m.file().fileKey().isBlank()
                        || m.file().originalName() == null || m.file().originalName().isBlank()
                        || m.file().contentType() == null || m.file().contentType().isBlank()
                        || m.file().fileSize() == null || m.file().fileSize() <= 0) {
                    throw new FeedbackMediaInvalidException();
                }
            }
            List<FileRegistrationResult> fileResults = registerMediaFiles(command.userId(),
                    command.media().stream().map(UpdateFeedbackCommand.MediaCommand::file).toList());
            feedbackMediaRepository.deleteAllByFeedbackId(feedback.getId());
            List<FeedbackMedia> newMedia = IntStream.range(0, command.media().size())
                    .mapToObj(i -> FeedbackMedia.create(
                            feedback.getId(),
                            command.media().get(i).mediaType(),
                            fileResults.get(i).fileId()
                    ))
                    .toList();
            feedbackMediaRepository.saveAll(newMedia);
        }

        log.info("event=feedback_update_complete feedbackId={}", feedback.getId());
        return feedback.getId();
    }

    @Override
    public void deleteFeedback(DeleteFeedbackCommand command) {
        log.debug("event=feedback_delete userId={} feedbackId={}", command.userId(), command.feedbackId());

        // 피드백 조회
        Feedback feedback = feedbackRepository.findById(command.feedbackId())
                .orElseThrow(() -> {
                    log.warn("event=feedback_delete_failed reason=not_found feedbackId={}", command.feedbackId());
                    return new FeedbackNotFoundException();
                });

        // path param 예약과 피드백 예약이 같은 코스인지 확인
        PtReservationQueryPort.ReservationInfo pathReservation = ptReservationQueryPort.findById(command.ptReservationId());
        PtReservationQueryPort.ReservationInfo feedbackReservation = ptReservationQueryPort.findById(feedback.getPtReservationId());
        if (!pathReservation.ptCourseId().equals(feedbackReservation.ptCourseId())) {
            throw new FeedbackNotFoundException();
        }

        // 트레이너 본인 피드백인지 확인
        Long trainerProfileId = trainerQueryPort.findTrainerProfileIdByUserId(command.userId())
                .orElseThrow(FeedbackForbiddenException::new);

        if (!feedback.getTrainerProfileId().equals(trainerProfileId)) {
            log.warn("event=feedback_delete_failed reason=forbidden userId={} feedbackId={}",
                    command.userId(), command.feedbackId());
            throw new FeedbackForbiddenException();
        }

        // 예약 전체가 완료된 경우 삭제 불가 (세션 종료 시각은 체크하지 않음 — 피드백은 세션 후 작성)
        if (feedbackReservation.status() == PtReservationStatus.COMPLETED) {
            log.warn("event=feedback_delete_failed reason=reservation_completed feedbackId={}", command.feedbackId());
            throw new FeedbackReservationCompletedException();
        }

        feedbackRepository.deleteById(command.feedbackId());
        log.info("event=feedback_delete_complete feedbackId={}", command.feedbackId());
    }

    // create/update 공통 미디어 파일 등록 — UploadedFileMetadataCommand 리스트를 직접 받음
    private List<FileRegistrationResult> registerMediaFiles(Long userId,
                                                             List<UploadedFileMetadataCommand> files) {
        List<CreateFileCommand> fileCommands = files.stream()
                .map(f -> new CreateFileCommand(
                        userId,
                        f.fileKey(),
                        f.originalName(),
                        f.contentType(),
                        f.fileSize(),
                        FileType.FEEDBACK_VIDEO
                ))
                .toList();

        List<FileRegistrationResult> results = fileUseCase.registerFiles(fileCommands);

        if (results.size() != files.size()) {
            log.error("event=feedback_media_register_failed reason=unexpected_result userId={} expected={} actual={}",
                    userId, files.size(), results.size());
            throw new IllegalStateException("미디어 파일 등록 결과가 올바르지 않습니다.");
        }

        return results;
    }
}
