package com.ssambbong.gymjjak.pt.feedback.application.service;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.pt.feedback.application.command.CreateFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.command.DeleteFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.command.UpdateFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtCurriculumQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.usecase.FeedbackCommandUseCase;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackAlreadyExistsException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackForbiddenException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackMediaInvalidException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackNotFoundException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackReservationCompletedException;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
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

        log.info("event=feedback_create_complete feedbackId={}", saved.getId());
        return saved.getId();
    }

    @Override
    public Long updateFeedback(UpdateFeedbackCommand command) {
        log.debug("event=feedback_update userId={} feedbackId={}", command.userId(), command.feedbackId());

        // 미디어 타입 중복 검증 (BEFORE/AFTER 각 1개)
        Set<FeedbackMediaType> mediaTypes = command.media().stream()
                .map(UpdateFeedbackCommand.MediaCommand::mediaType)
                .collect(Collectors.toSet());
        if (mediaTypes.size() != command.media().size()) {
            throw new FeedbackMediaInvalidException();
        }

        // 피드백 조회
        Feedback feedback = feedbackRepository.findById(command.feedbackId())
                .orElseThrow(() -> {
                    log.warn("event=feedback_update_failed reason=not_found feedbackId={}", command.feedbackId());
                    return new FeedbackNotFoundException();
                });

        // path parameter의 예약 ID와 피드백의 예약 ID 일치 확인
        if (!feedback.getPtReservationId().equals(command.ptReservationId())) {
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

        // 미디어 파일 필수값 검증
        for (UpdateFeedbackCommand.MediaCommand m : command.media()) {
            if (m.file() == null || m.file().fileKey() == null || m.file().fileKey().isBlank()
                    || m.file().originalName() == null || m.file().originalName().isBlank()
                    || m.file().contentType() == null || m.file().contentType().isBlank()
                    || m.file().fileSize() == null || m.file().fileSize() <= 0) {
                throw new FeedbackMediaInvalidException();
            }
        }

        // 미디어 파일 일괄 등록
        List<FileRegistrationResult> fileResults = registerMediaFiles(command.userId(),
                command.media().stream().map(UpdateFeedbackCommand.MediaCommand::file).toList());

        // 피드백 내용 수정
        feedback.update(command.content());
        feedbackRepository.update(feedback);

        // 기존 미디어 전체 삭제 후 신규 등록 (교체)
        feedbackMediaRepository.deleteAllByFeedbackId(feedback.getId());
        List<FeedbackMedia> newMedia = IntStream.range(0, command.media().size())
                .mapToObj(i -> FeedbackMedia.create(
                        feedback.getId(),
                        command.media().get(i).mediaType(),
                        fileResults.get(i).fileId()
                ))
                .toList();
        feedbackMediaRepository.saveAll(newMedia);

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

        // path parameter의 예약 ID와 피드백의 예약 ID 일치 확인
        if (!feedback.getPtReservationId().equals(command.ptReservationId())) {
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

        // 예약이 COMPLETED이면 삭제 불가
        PtReservationQueryPort.ReservationInfo reservation =
                ptReservationQueryPort.findById(command.ptReservationId());
        if (reservation.status() == PtReservationStatus.COMPLETED) {
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
