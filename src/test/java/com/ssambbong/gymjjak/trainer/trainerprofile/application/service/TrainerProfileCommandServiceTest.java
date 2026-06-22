package com.ssambbong.gymjjak.trainer.trainerprofile.application.service;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.UpdateProfileImageFileCommand;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.UpdateTrainerProfileCommand;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerAwardRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerCertificationRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.ssambbong.gymjjak.trainer.trainerprofile.application.command.ProfileImageUpdateAction.KEEP;
import static com.ssambbong.gymjjak.trainer.trainerprofile.application.command.ProfileImageUpdateAction.REPLACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TrainerProfileCommandServiceTest {

    @Mock
    private TrainerProfileRepository trainerProfileRepository;

    @Mock
    private TrainerCertificationRepository trainerCertificationRepository;

    @Mock
    private TrainerAwardRepository trainerAwardRepository;

    @Mock
    private FileUseCase fileUseCase;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private TrainerProfileCommandService service;

    @Test
    void 프로필_이미지를_유지하고_수정_가능한_정보를_변경한다() {
        // given
        TrainerProfile profile = createProfile();

        UpdateTrainerProfileCommand command =
                new UpdateTrainerProfileCommand(
                        2L,
                        KEEP,
                        null,
                        List.of(" NSCA-CPT ", "ACSM"),
                        List.of("2025 피트니스 대회 우승"),
                        "  체형 교정 전문 트레이너입니다.  "
                );

        when(trainerProfileRepository.findByUserId(2L))
                .thenReturn(Optional.of(profile));

        when(trainerProfileRepository.save(any(TrainerProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        executeTransactionCallback();

        // when
        Long result = service.updateMyTrainerProfile(command);

        // then
        assertThat(result).isEqualTo(7L);

        ArgumentCaptor<TrainerProfile> profileCaptor =
                ArgumentCaptor.forClass(TrainerProfile.class);

        verify(trainerProfileRepository)
                .save(profileCaptor.capture());

        TrainerProfile savedProfile =
                profileCaptor.getValue();

        assertThat(savedProfile.getTrainerProfileId()).isEqualTo(7L);
        assertThat(savedProfile.getProfileFileId()).isEqualTo(100L);
        assertThat(savedProfile.getIntroduction())
                .isEqualTo("체형 교정 전문 트레이너입니다.");

        verify(trainerCertificationRepository)
                .deleteAllAdditionalByTrainerProfileId(7L);

        verify(trainerCertificationRepository).saveAll(
                argThat(certifications ->
                        certifications.size() == 2
                                && certifications.get(0).getName()
                                .equals("NSCA-CPT")
                                && certifications.get(1).getName()
                                .equals("ACSM")
                )
        );

        verify(trainerAwardRepository)
                .deleteAllByTrainerProfileId(7L);

        verify(trainerAwardRepository).saveAll(
                argThat(awards ->
                        awards.size() == 1
                                && awards.get(0).getName()
                                .equals("2025 피트니스 대회 우승")
                )
        );

        verifyNoInteractions(fileUseCase);
    }

    private TrainerProfile createProfile() {
        return TrainerProfile.restore(
                7L,
                2L,
                9L,
                100L,
                "임시 트레이너",
                "기존 자기소개",
                BigDecimal.ZERO,
                0,
                TrainerProfileStatus.ACTIVE
        );
    }

    private void executeTransactionCallback() {
        when(transactionTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    TransactionCallback<?> callback =
                            invocation.getArgument(0);

                    return callback.doInTransaction(
                            mock(TransactionStatus.class)
                    );
                });
    }

    @Test
    void 새_프로필_이미지를_등록하고_DB_수정_후_기존_이미지를_삭제한다() {
        // given
        TrainerProfile profile = createProfile();

        UpdateProfileImageFileCommand imageFile =
                new UpdateProfileImageFileCommand(
                        "uploads/profiles/trainers/2/new-image-key",
                        "new-profile.png",
                        "image/png",
                        524288L
                );

        UpdateTrainerProfileCommand command =
                new UpdateTrainerProfileCommand(
                        2L,
                        REPLACE,
                        imageFile,
                        null,
                        null,
                        null
                );

        CreateFileCommand expectedFileCommand =
                new CreateFileCommand(
                        2L,
                        imageFile.fileKey(),
                        imageFile.originalName(),
                        imageFile.contentType(),
                        imageFile.fileSize(),
                        FileType.PROFILE_IMAGE
                );

        when(trainerProfileRepository.findByUserId(2L))
                .thenReturn(Optional.of(profile));

        when(fileUseCase.registerFiles(
                List.of(expectedFileCommand)
        )).thenReturn(
                List.of(
                        new FileRegistrationResult(
                                200L,
                                FileType.PROFILE_IMAGE
                        )
                )
        );

        when(trainerProfileRepository.save(any(TrainerProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        executeTransactionCallback();

        // when
        Long result =
                service.updateMyTrainerProfile(command);

        // then
        assertThat(result).isEqualTo(7L);

        ArgumentCaptor<TrainerProfile> profileCaptor =
                ArgumentCaptor.forClass(TrainerProfile.class);

        verify(trainerProfileRepository)
                .save(profileCaptor.capture());

        TrainerProfile savedProfile =
                profileCaptor.getValue();

        assertThat(savedProfile.getProfileFileId())
                .isEqualTo(200L);

        assertThat(savedProfile.getIntroduction())
                .isEqualTo("기존 자기소개");

        verify(fileUseCase).registerFiles(
                List.of(expectedFileCommand)
        );

        // DB 수정 성공 후 기존 파일만 삭제
        verify(fileUseCase).deleteFile(100L);
        verify(fileUseCase, never()).deleteFile(200L);

        // null 목록은 기존 데이터 유지
        verifyNoInteractions(
                trainerCertificationRepository,
                trainerAwardRepository
        );

        InOrder inOrder = inOrder(
                fileUseCase,
                trainerProfileRepository
        );

        inOrder.verify(trainerProfileRepository)
                .findByUserId(2L);

        inOrder.verify(fileUseCase)
                .registerFiles(List.of(expectedFileCommand));

        inOrder.verify(trainerProfileRepository)
                .save(any(TrainerProfile.class));

        inOrder.verify(fileUseCase)
                .deleteFile(100L);
    }

    @Test
    void DB_수정에_실패하면_신규_이미지만_보상_삭제한다() {
        // given
        TrainerProfile profile = createProfile();

        UpdateProfileImageFileCommand imageFile =
                new UpdateProfileImageFileCommand(
                        "uploads/profiles/trainers/2/new-image-key",
                        "new-profile.png",
                        "image/png",
                        524288L
                );

        UpdateTrainerProfileCommand command =
                new UpdateTrainerProfileCommand(
                        2L,
                        REPLACE,
                        imageFile,
                        null,
                        null,
                        null
                );

        CreateFileCommand expectedFileCommand =
                new CreateFileCommand(
                        2L,
                        imageFile.fileKey(),
                        imageFile.originalName(),
                        imageFile.contentType(),
                        imageFile.fileSize(),
                        FileType.PROFILE_IMAGE
                );

        when(trainerProfileRepository.findByUserId(2L))
                .thenReturn(Optional.of(profile));

        when(fileUseCase.registerFiles(
                List.of(expectedFileCommand)
        )).thenReturn(
                List.of(
                        new FileRegistrationResult(
                                200L,
                                FileType.PROFILE_IMAGE
                        )
                )
        );

        executeTransactionCallback();

        RuntimeException dbException =
                new RuntimeException("DB 저장 실패");

        when(trainerProfileRepository.save(any(TrainerProfile.class)))
                .thenThrow(dbException);

        // when & then
        assertThatThrownBy(() ->
                service.updateMyTrainerProfile(command)
        )
                .isSameAs(dbException)
                .hasMessage("DB 저장 실패");

        // 새로 등록한 파일은 보상 삭제
        verify(fileUseCase).deleteFile(200L);

        // DB가 기존 파일을 계속 참조하므로 기존 파일은 유지
        verify(fileUseCase, never()).deleteFile(100L);

        // 프로필 저장에서 실패했으므로 목록 교체 미실행
        verifyNoInteractions(
                trainerCertificationRepository,
                trainerAwardRepository
        );

        InOrder inOrder = inOrder(
                fileUseCase,
                trainerProfileRepository
        );

        inOrder.verify(trainerProfileRepository)
                .findByUserId(2L);

        inOrder.verify(fileUseCase)
                .registerFiles(List.of(expectedFileCommand));

        inOrder.verify(trainerProfileRepository)
                .save(any(TrainerProfile.class));

        inOrder.verify(fileUseCase)
                .deleteFile(200L);
    }
}