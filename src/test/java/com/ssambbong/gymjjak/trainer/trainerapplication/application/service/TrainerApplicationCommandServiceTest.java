package com.ssambbong.gymjjak.trainer.trainerapplication.application.service;

import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.ocr.application.usecase.OcrUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.RejectTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.ApprovedTrainerProfilePort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationOrganizationPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationOrganizationTrainerPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationUserPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.InvalidTrainerApplicationException;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.TrainerApplicationNotFoundException;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.TrainerApplicationStatusConflictException;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository.TrainerApplicationRepository;
import com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.metrics.TrainerApplicationMetric;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerApplicationCommandServiceTest {

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
    private TrainerApplicationOrganizationPort trainerApplicationOrganizationPort;

    @Mock
    private ApprovedTrainerProfilePort approvedTrainerProfilePort;

    @Mock
    private TrainerApplicationOrganizationTrainerPort trainerApplicationOrganizationTrainerPort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TrainerApplicationMetric trainerApplicationMetric;

    @InjectMocks
    private TrainerApplicationCommandService service;

    private TrainerApplication pendingApplication() {
        return TrainerApplication.builder()
                .trainerApplicationId(1L)
                .userId(10L)
                .organizationId(1L)
                .profileFileId(100L)
                .certificateFileId(200L)
                .qualifications(List.of("생활스포츠지도사 2급"))
                .awardHistories(List.of())
                .introduction("트레이너 신청입니다.")
                .status(TrainerApplicationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("PENDING 상태의 트레이너 신청을 반려한다")
    void rejectTrainerApplication_success() {
        // given
        RejectTrainerApplicationCommand command =
                new RejectTrainerApplicationCommand(
                        1L,
                        99L,
                        "  자격증 이미지가 불명확합니다.  "
                );

        when(trainerApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(pendingApplication()));
        when(trainerApplicationOrganizationPort.findOrganizationIdByAccountId(99L))
                .thenReturn(1L);

        // when
        service.rejectTrainerApplication(command);

        // then
        ArgumentCaptor<TrainerApplication> captor =
                ArgumentCaptor.forClass(TrainerApplication.class);

        verify(trainerApplicationRepository).save(captor.capture());

        TrainerApplication saved = captor.getValue();

        assertThat(saved.getTrainerApplicationId()).isEqualTo(1L);
        assertThat(saved.getStatus())
                .isEqualTo(TrainerApplicationStatus.REJECTED);
        assertThat(saved.getRejectReason())
                .isEqualTo("자격증 이미지가 불명확합니다.");
        assertThat(saved.getReviewedBy()).isEqualTo(99L);
        assertThat(saved.getReviewedAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 트레이너 신청은 반려할 수 없다")
    void rejectTrainerApplication_fail_notFound() {
        // given
        RejectTrainerApplicationCommand command =
                new RejectTrainerApplicationCommand(
                        999L,
                        99L,
                        "반려 사유"
                );

        when(trainerApplicationRepository.findByIdForUpdate(999L))
                .thenReturn(Optional.empty());
        when(trainerApplicationOrganizationPort.findOrganizationIdByAccountId(99L))
                .thenReturn(1L);

        // when & then
        assertThatThrownBy(() ->
                service.rejectTrainerApplication(command)
        ).isInstanceOf(TrainerApplicationNotFoundException.class);

        verify(trainerApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("APPROVED 상태의 트레이너 신청은 반려할 수 없다")
    void rejectTrainerApplication_fail_statusConflict() {
        // given
        TrainerApplication approvedApplication =
                TrainerApplication.builder()
                        .trainerApplicationId(1L)
                        .userId(10L)
                        .organizationId(1L)
                        .certificateFileId(200L)
                        .qualifications(List.of())
                        .awardHistories(List.of())
                        .introduction("트레이너 신청입니다.")
                        .status(TrainerApplicationStatus.APPROVED)
                        .reviewedBy(50L)
                        .build();

        RejectTrainerApplicationCommand command =
                new RejectTrainerApplicationCommand(
                        1L,
                        99L,
                        "반려 사유"
                );

        when(trainerApplicationRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(approvedApplication));
        when(trainerApplicationOrganizationPort.findOrganizationIdByAccountId(99L))
                .thenReturn(1L);

        // when & then
        assertThatThrownBy(() ->
                service.rejectTrainerApplication(command)
        ).isInstanceOf(
                TrainerApplicationStatusConflictException.class
        );

        verify(trainerApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("반려 Command가 null이면 실패한다")
    void rejectTrainerApplication_fail_nullCommand() {
        assertThatThrownBy(() ->
                service.rejectTrainerApplication(null)
        ).isInstanceOf(InvalidTrainerApplicationException.class);

        verifyNoInteractions(trainerApplicationRepository);
    }
}
