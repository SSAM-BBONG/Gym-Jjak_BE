/*
package com.ssambbong.gymjjak.pt.feedback.application.service;

import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.pt.feedback.application.command.UpdateFeedbackCommand;
import com.ssambbong.gymjjak.pt.feedback.application.command.UploadedFileMetadataCommand;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtCurriculumQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.PtReservationQueryPort;
import com.ssambbong.gymjjak.pt.feedback.application.port.TrainerQueryPort;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackForbiddenException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackMediaInvalidException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackNotFoundException;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMediaType;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackStatus;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackMediaRepository;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackCommandServiceTest {

    @Mock private FeedbackRepository feedbackRepository;
    @Mock private FeedbackMediaRepository feedbackMediaRepository;
    @Mock private PtReservationQueryPort ptReservationQueryPort;
    @Mock private PtCurriculumQueryPort ptCurriculumQueryPort;
    @Mock private TrainerQueryPort trainerQueryPort;
    @Mock private FileUseCase fileUseCase;
    @Mock private Clock clock;

    @InjectMocks
    private FeedbackCommandService feedbackCommandService;

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(Instant.parse("2026-07-17T00:00:00Z"));
        lenient().when(clock.getZone()).thenReturn(ZoneId.of("Asia/Seoul"));
    }

    private static final Long USER_ID = 1L;
    private static final Long PT_RESERVATION_ID = 10L;
    private static final Long FEEDBACK_ID = 20L;
    private static final Long TRAINER_PROFILE_ID = 5L;
    private static final Long PT_COURSE_ID = 100L;

    // path parameter 예약과 피드백 예약이 "같은 코스" 검증을 통과시키기 위한 기본 조회 결과.
    // "완료된 세션은 피드백 수정 불가" 규칙에 안 걸리도록 진행중 + 종료시각은 미래로 둔다.
    private PtReservationQueryPort.ReservationInfo reservationInfo(Long ptCourseId) {
        // clock 목(2026-07-17 고정)보다 항상 뒤에 오도록 고정된 미래 시각을 쓴다.
        return new PtReservationQueryPort.ReservationInfo(
                ptCourseId, TRAINER_PROFILE_ID, USER_ID,
                PtReservationStatus.IN_PROGRESS, LocalDateTime.of(2099, 1, 1, 0, 0));
    }

    private UpdateFeedbackCommand defaultUpdateCommand() {
        return new UpdateFeedbackCommand(
                USER_ID, PT_RESERVATION_ID, FEEDBACK_ID,
                "수정된 피드백 내용",
                List.of(
                        new UpdateFeedbackCommand.MediaCommand(
                                new UploadedFileMetadataCommand(
                                        "uploads/feedbacks/videos/1/before.mp4",
                                        "before.mp4", "video/mp4", 1024L
                                ),
                                FeedbackMediaType.BEFORE
                        ),
                        new UpdateFeedbackCommand.MediaCommand(
                                new UploadedFileMetadataCommand(
                                        "uploads/feedbacks/videos/1/after.mp4",
                                        "after.mp4", "video/mp4", 2048L
                                ),
                                FeedbackMediaType.AFTER
                        )
                )
        );
    }

    private Feedback existingFeedback() {
        return Feedback.restore(
                FEEDBACK_ID, PT_RESERVATION_ID, 1L,
                TRAINER_PROFILE_ID, 2L,
                "기존 피드백 내용", FeedbackStatus.ACTIVE, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("피드백 수정 시 feedbackId가 반환되어야 한다")
    void updateFeedback_success() {

        // given
        when(feedbackRepository.findById(FEEDBACK_ID)).thenReturn(Optional.of(existingFeedback()));
        when(ptReservationQueryPort.findById(PT_RESERVATION_ID)).thenReturn(reservationInfo(PT_COURSE_ID));
        when(trainerQueryPort.findTrainerProfileIdByUserId(USER_ID))
                .thenReturn(Optional.of(TRAINER_PROFILE_ID));
        when(fileUseCase.registerFiles(any()))
                .thenReturn(List.of(
                        new FileRegistrationResult(101L, FileType.FEEDBACK_VIDEO),
                        new FileRegistrationResult(102L, FileType.FEEDBACK_VIDEO)
                ));

        // when
        Long result = feedbackCommandService.updateFeedback(defaultUpdateCommand());

        // then
        assertEquals(FEEDBACK_ID, result);
        verify(feedbackRepository).update(any(Feedback.class));
        verify(feedbackMediaRepository).deleteAllByFeedbackId(FEEDBACK_ID);
        verify(feedbackMediaRepository).saveAll(any());
    }

    @Test
    @DisplayName("피드백이 존재하지 않으면 FeedbackNotFoundException이 발생한다")
    void updateFeedback_feedbackNotFound_throwsException() {

        // given
        when(feedbackRepository.findById(FEEDBACK_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(FeedbackNotFoundException.class,
                () -> feedbackCommandService.updateFeedback(defaultUpdateCommand()));

        verify(feedbackRepository, never()).update(any());
    }

    @Test
    @DisplayName("path parameter의 예약 ID와 피드백의 예약 ID가 다르면 FeedbackNotFoundException이 발생한다")
    void updateFeedback_reservationMismatch_throwsException() {

        // given — 피드백의 ptReservationId가 요청의 ptReservationId와 다름
        Feedback mismatchFeedback = Feedback.restore(
                FEEDBACK_ID, 999L, 1L, TRAINER_PROFILE_ID, 2L,
                "내용", FeedbackStatus.ACTIVE, LocalDateTime.now()
        );
        when(feedbackRepository.findById(FEEDBACK_ID)).thenReturn(Optional.of(mismatchFeedback));
        // path 예약(10L)과 피드백 예약(999L)이 서로 다른 코스를 가리키게 한다.
        when(ptReservationQueryPort.findById(PT_RESERVATION_ID)).thenReturn(reservationInfo(PT_COURSE_ID));
        when(ptReservationQueryPort.findById(999L)).thenReturn(reservationInfo(200L));

        // when & then
        assertThrows(FeedbackNotFoundException.class,
                () -> feedbackCommandService.updateFeedback(defaultUpdateCommand()));

        verify(trainerQueryPort, never()).findTrainerProfileIdByUserId(any());
        verify(feedbackRepository, never()).update(any());
    }

    @Test
    @DisplayName("트레이너 프로필이 없으면 FeedbackForbiddenException이 발생한다")
    void updateFeedback_trainerNotFound_throwsException() {

        // given
        when(feedbackRepository.findById(FEEDBACK_ID)).thenReturn(Optional.of(existingFeedback()));
        when(ptReservationQueryPort.findById(PT_RESERVATION_ID)).thenReturn(reservationInfo(PT_COURSE_ID));
        when(trainerQueryPort.findTrainerProfileIdByUserId(USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(FeedbackForbiddenException.class,
                () -> feedbackCommandService.updateFeedback(defaultUpdateCommand()));

        verify(feedbackRepository, never()).update(any());
    }

    @Test
    @DisplayName("본인 피드백이 아니면 FeedbackForbiddenException이 발생한다")
    void updateFeedback_forbidden_throwsException() {

        // given — 피드백의 trainerProfileId=5, 요청자의 trainerProfileId=99
        when(feedbackRepository.findById(FEEDBACK_ID)).thenReturn(Optional.of(existingFeedback()));
        when(ptReservationQueryPort.findById(PT_RESERVATION_ID)).thenReturn(reservationInfo(PT_COURSE_ID));
        when(trainerQueryPort.findTrainerProfileIdByUserId(USER_ID))
                .thenReturn(Optional.of(99L)); // 다른 트레이너

        // when & then
        assertThrows(FeedbackForbiddenException.class,
                () -> feedbackCommandService.updateFeedback(defaultUpdateCommand()));

        verify(feedbackRepository, never()).update(any());
    }

    @Test
    @DisplayName("미디어 타입이 중복되면 FeedbackMediaInvalidException이 발생한다")
    void updateFeedback_duplicateMediaType_throwsException() {

        // given — BEFORE 두 개
        UpdateFeedbackCommand command = new UpdateFeedbackCommand(
                USER_ID, PT_RESERVATION_ID, FEEDBACK_ID,
                "수정된 내용",
                List.of(
                        new UpdateFeedbackCommand.MediaCommand(
                                new UploadedFileMetadataCommand(
                                        "uploads/feedbacks/videos/1/a.mp4", "a.mp4", "video/mp4", 1024L
                                ),
                                FeedbackMediaType.BEFORE
                        ),
                        new UpdateFeedbackCommand.MediaCommand(
                                new UploadedFileMetadataCommand(
                                        "uploads/feedbacks/videos/1/b.mp4", "b.mp4", "video/mp4", 1024L
                                ),
                                FeedbackMediaType.BEFORE // 중복
                        )
                )
        );

        // when & then
        assertThrows(FeedbackMediaInvalidException.class,
                () -> feedbackCommandService.updateFeedback(command));

        verify(feedbackRepository, never()).findById(any());
    }

    @Test
    @DisplayName("파일 메타데이터 필수값이 누락되면 FeedbackMediaInvalidException이 발생한다")
    void updateFeedback_missingFileMetadata_throwsException() {

        // given — fileSize가 0 (유효하지 않음)
        UpdateFeedbackCommand command = new UpdateFeedbackCommand(
                USER_ID, PT_RESERVATION_ID, FEEDBACK_ID,
                "수정된 내용",
                List.of(
                        new UpdateFeedbackCommand.MediaCommand(
                                new UploadedFileMetadataCommand(
                                        "uploads/feedbacks/videos/1/before.mp4",
                                        "before.mp4", "video/mp4", 0L // fileSize <= 0
                                ),
                                FeedbackMediaType.BEFORE
                        )
                )
        );

        when(feedbackRepository.findById(FEEDBACK_ID)).thenReturn(Optional.of(existingFeedback()));
        when(ptReservationQueryPort.findById(PT_RESERVATION_ID)).thenReturn(reservationInfo(PT_COURSE_ID));
        when(trainerQueryPort.findTrainerProfileIdByUserId(USER_ID))
                .thenReturn(Optional.of(TRAINER_PROFILE_ID));

        // when & then
        assertThrows(FeedbackMediaInvalidException.class,
                () -> feedbackCommandService.updateFeedback(command));

        verify(fileUseCase, never()).registerFiles(any());
        verify(feedbackRepository, never()).update(any());
    }
}
*/
