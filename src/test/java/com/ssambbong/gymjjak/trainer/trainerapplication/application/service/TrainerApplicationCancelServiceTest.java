package com.ssambbong.gymjjak.trainer.trainerapplication.application.service;

import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.ocr.application.usecase.OcrUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CancelTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.ApprovedTrainerProfilePort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationUserPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.service.TrainerApplicationCommandService;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.ForbiddenTrainerApplicationCancelException;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.TrainerApplicationNotFoundException;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.TrainerApplicationStatusConflictException;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository.TrainerApplicationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerApplicationCancelServiceTest {

    @Mock
    private FileUseCase fileUseCase;

    @Mock
    private OcrUseCase ocrUseCase;

    @Mock
    private TrainerApplicationRepository trainerApplicationRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private TrainerApplicationUserPort trainerApplicationUserPort;

    @Mock
    private ApprovedTrainerProfilePort approvedTrainerProfilePort;

    @InjectMocks
    private TrainerApplicationCommandService service;

    @BeforeEach
    void setUp() {
        when(transactionTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback =
                            invocation.getArgument(0);

                    return callback.doInTransaction(
                            mock(TransactionStatus.class)
                    );
                });
    }

    private TrainerApplication pendingApplication() {
        return TrainerApplication.builder()
                .trainerApplicationId(1L)
                .userId(10L)
                .profileFileId(100L)
                .certificateFileId(200L)
                .qualifications(List.of("생활스포츠지도사 2급"))
                .awardHistories(List.of())
                .introduction("트레이너 신청입니다.")
                .status(TrainerApplicationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("본인의 PENDING 신청을 하드 삭제하고 연결 파일을 정리한다")
    void cancelTrainerApplication_success() {
        // given
        CancelTrainerApplicationCommand command =
                new CancelTrainerApplicationCommand(1L, 10L);

        when(trainerApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(pendingApplication()));

        // when
        service.cancelTrainerApplication(command);

        // then
        verify(trainerApplicationRepository).deleteById(1L);
        verify(fileUseCase).deleteFile(100L);
        verify(fileUseCase).deleteFile(200L);
    }

    @Test
    @DisplayName("다른 사용자의 신청은 취소할 수 없다")
    void cancelTrainerApplication_fail_notOwner() {
        // given
        when(trainerApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(pendingApplication()));

        CancelTrainerApplicationCommand command =
                new CancelTrainerApplicationCommand(
                        1L,
                        999L
                );

        // when & then
        assertThatThrownBy(() ->
                service.cancelTrainerApplication(command)
        ).isInstanceOf(
                ForbiddenTrainerApplicationCancelException.class
        );

        verify(trainerApplicationRepository, never()).deleteById(anyLong());
        verifyNoInteractions(fileUseCase);
    }

    @Test
    @DisplayName("승인된 신청은 취소할 수 없다")
    void cancelTrainerApplication_fail_notPending() {
        // given
        TrainerApplication approved =
                TrainerApplication.builder()
                        .trainerApplicationId(1L)
                        .userId(10L)
                        .certificateFileId(200L)
                        .qualifications(List.of())
                        .awardHistories(List.of())
                        .introduction("신청서")
                        .status(TrainerApplicationStatus.APPROVED)
                        .build();

        when(trainerApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(approved));

        // when & then
        assertThatThrownBy(() ->
                service.cancelTrainerApplication(
                        new CancelTrainerApplicationCommand(1L, 10L)
                )
        ).isInstanceOf(
                TrainerApplicationStatusConflictException.class
        );

        verify(trainerApplicationRepository, never()).deleteById(anyLong());
        verifyNoInteractions(fileUseCase);
    }

    @Test
    @DisplayName("존재하지 않는 신청은 취소할 수 없다")
    void cancelTrainerApplication_fail_notFound() {
        when(trainerApplicationRepository.findByIdForUpdate(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.cancelTrainerApplication(
                        new CancelTrainerApplicationCommand(999L, 10L)
                )
        ).isInstanceOf(
                TrainerApplicationNotFoundException.class
        );

        verify(trainerApplicationRepository, never()).deleteById(anyLong());
        verifyNoInteractions(fileUseCase);
    }

    @Test
    @DisplayName("파일 정리에 실패해도 신청 취소는 완료된다")
    void cancelTrainerApplication_fileCleanupFailure_doesNotFail() {
        // given
        when(trainerApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(pendingApplication()));

        doThrow(new RuntimeException("S3 delete failed"))
                .when(fileUseCase)
                .deleteFile(100L);

        // when & then
        assertThatCode(() ->
                service.cancelTrainerApplication(
                        new CancelTrainerApplicationCommand(1L, 10L)
                )
        ).doesNotThrowAnyException();

        verify(trainerApplicationRepository).deleteById(1L);
        verify(fileUseCase).deleteFile(100L);
        verify(fileUseCase).deleteFile(200L);
    }
}
