package com.ssambbong.gymjjak.trainer.trainerprofile.application.service;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.ProfileImageUpdateAction;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.UpdateProfileImageFileCommand;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.UpdateTrainerProfileCommand;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase.TrainerProfileCommandUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception.InvalidTrainerProfileException;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception.TrainerProfileNotFoundException;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerAward;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertification;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerAwardRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerCertificationRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.repository.TrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static com.ssambbong.gymjjak.trainer.trainerprofile.application.command.ProfileImageUpdateAction.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainerProfileCommandService implements TrainerProfileCommandUseCase {
    /* Comment
    *   updateMyTrainerProfile 트랜잭션템플릿 사용
    *   - 프로필, 추가 자격증, 수상 경력 DB 변경: 하나의 트랜잭션
    *   - 신규 파일 등록 실패: DB update 시작 안 함
    *   - DB 수정 실패: 신규 파일 삭제
    *   - 기존 파일 삭제 실패: 수정 성공 유지, 로그로 기록
    * */

    private final TrainerProfileRepository trainerProfileRepository;
    private final TrainerCertificationRepository trainerCertificationRepository;
    private final TrainerAwardRepository trainerAwardRepository;
    private final FileUseCase fileUseCase;
    private final TransactionTemplate transactionTemplate;

    @Override
    public Long updateMyTrainerProfile(UpdateTrainerProfileCommand command) {
        // command 값 검증
        validateRequiredCommand(command);

        // 이미지 상태값 검증
        validateProfileImageUpdate(
                command.profileImageAction(),
                command.profileImageFile()
        );
        // 프로필 조회
        TrainerProfile profile =
                findTrainerProfile(command.requesterId());

        // 수정전 프로필 이미지 파일id
        Long oldProfileFileId = profile.getProfileFileId();

        // 새로운 프로필 이미지 파일 등록
        Long newProfileFileId  =
                registerNewProfileImage(command);

        // 수정 Action 확인하여 fileId 값 결정
        Long updatedProfileFileId =
                resolveUpdatedProfileFileId(
                        command.profileImageAction(),
                        profile.getProfileFileId(),
                        newProfileFileId
                );

        // 자기소개 수정 여부 확인
        String updatedIntroduction =
                command.introduction() == null
                        ? profile.getIntroduction()
                        : command.introduction();

        Long trainerProfileId;

        // DB 트랜잭션 실시, 트레이너 프로필은 트랜잭션 따로 관리
        try {
            trainerProfileId = transactionTemplate.execute(status ->
                    updateProfileInTransaction(
                            profile,
                            updatedProfileFileId,
                            updatedIntroduction,
                            command
                    )
            );

            if (trainerProfileId == null) {
                throw new IllegalStateException(
                        "트레이너 프로필 수정 결과가 존재하지 않습니다."
                );
            }
        } catch (RuntimeException exception) {
            // DB 수정 실패 시 새로 등록한 파일 정리
            deleteFileSafely(
                    newProfileFileId,
                    exception
            );

            throw exception;
        }

        // DB 수정 성공 후, 기존 파일 정리
        deleteOldProfileImageSafely(
                command.profileImageAction(),
                oldProfileFileId,
                newProfileFileId
        );

        log.info(
                "event=trainer_profile_update_succeeded, " +
                        "trainerProfileId={}, requesterId={}, imageAction={}",
                trainerProfileId,
                command.requesterId(),
                command.profileImageAction()
        );

        return trainerProfileId;
    }

    private void validateRequiredCommand(UpdateTrainerProfileCommand command) {
        if (command == null) {
            throw new InvalidTrainerProfileException("command는 필수입니다.");
        }

        if (command.requesterId() == null) {
            throw new InvalidTrainerProfileException("requesterId는 필수입니다.");
        }
    }

    // 이전 프로필 이미지 파일 삭제 메서드
    private void deleteOldProfileImageSafely(
            ProfileImageUpdateAction action,
            Long oldProfileFileId,
            Long newProfileFileId) {

        if (action == KEEP || oldProfileFileId == null) {
            return;
        }

        if (oldProfileFileId.equals(newProfileFileId)) {
            return;
        }

        try {
            fileUseCase.deleteFile(oldProfileFileId);
        } catch (RuntimeException exception) {
            log.error(
                    "event=trainer_profile_old_file_cleanup_failed, " +
                            "oldProfileFileId={}, imageAction={}",
                    oldProfileFileId,
                    action,
                    exception
            );
        }
    }

    // 트레이너 프로필 수정 DB 트랜잭션 내부 처리
    private Long updateProfileInTransaction(
            TrainerProfile profile,
            Long updatedProfileFileId,
            String updatedIntroduction,
            UpdateTrainerProfileCommand command
    ) {
        // id 추출
        Long trainerProfileId =
                profile.getTrainerProfileId();

        // 수정된 트레이너 프로필 domain
        TrainerProfile updatedProfile =
                profile.updateEditableInfo(
                        updatedProfileFileId,
                        updatedIntroduction
                );

        // 트레이너 프로필 DB 저장
        TrainerProfile savedProfile = trainerProfileRepository.save(updatedProfile);

        // 자격증 변경 사항 DB 저장
        replaceAdditionalCertifications(
                trainerProfileId,
                command.additionalCertifications()
        );
        // 수상경력 변경사항 DB 저장
        replaceAwards(
                trainerProfileId,
                command.awardHistories()
        );

        return savedProfile.getTrainerProfileId();
    }

    // 수상경력 수정
    private void replaceAwards(Long trainerProfileId, List<String> names) {
        if (names == null) {
            return;
        }
        // 기존 수상 기록 삭제
        trainerAwardRepository.deleteAllByTrainerProfileId(
                trainerProfileId
        );
        // 수정된 수상 기록 목록 생성
        List<TrainerAward> newAwards =
                names.stream()
                        .map(name ->
                                TrainerAward.create(
                                        trainerProfileId,
                                        name
                                )
                        )
                        .toList();

        trainerAwardRepository.saveAll(newAwards );
    }

    // 자격증 수정
    private void replaceAdditionalCertifications(Long trainerProfileId, List<String> names) {
        if (names == null) {
            return;
        }
        // 기존 값 삭제
        trainerCertificationRepository
                .deleteAllAdditionalByTrainerProfileId(trainerProfileId);

        // 수정된 자격증 목록 생성
        List<TrainerCertification> newCertifications =
                names.stream()
                        .map(name ->
                                TrainerCertification.additional(
                                        trainerProfileId,
                                        name
                                )
                        )
                        .toList();
        // DB 저장
        trainerCertificationRepository.saveAll(newCertifications);
    }

    // 수정된 이미지 Action 값 분기 처리
    private Long resolveUpdatedProfileFileId(
            ProfileImageUpdateAction action,
            Long currentProfileFileId,
            Long newProfileFileId
    ) {
        return switch (action) {
            case KEEP -> currentProfileFileId;
            case REPLACE -> newProfileFileId;
            case DELETE -> null;
        };
    }

    // 프로필 이미지 수정 검증
    private void validateProfileImageUpdate(
            ProfileImageUpdateAction action,
            UpdateProfileImageFileCommand file
    ) {
        if (action == null) {
            throw new InvalidTrainerProfileException(
                    "프로필 이미지 수정 방식은 null 일 수 없습니다."
            );
        }

        if (action == REPLACE && file == null) {
            throw new InvalidTrainerProfileException(
                    "프로필 이미지를 교체하려면 새 파일 정보가 필요합니다."
            );
        }

        if (action != REPLACE && file != null) {
            throw new InvalidTrainerProfileException(
                    "프로필 이미지 파일 정보는 REPLACE 요청에서만 전달할 수 있습니다."
            );
        }
    }
    // 트레이너 프로필 조회
    private TrainerProfile findTrainerProfile(
            Long requesterId
    ) {
        return trainerProfileRepository.findByUserId(requesterId)
                .orElseThrow(() ->
                        new TrainerProfileNotFoundException(requesterId)
                );
    }

    // 파일 등록
    private Long registerNewProfileImage(
            UpdateTrainerProfileCommand command
    ) {
        if (command.profileImageAction()
                != REPLACE) {
            return null;
        }

        UpdateProfileImageFileCommand file =
                command.profileImageFile();

        // 파일 등록
        List<FileRegistrationResult> results  =
                fileUseCase.registerFiles(
                        List.of(
                                new CreateFileCommand(
                                        command.requesterId(),
                                        file.fileKey(),
                                        file.originalName(),
                                        file.contentType(),
                                        file.fileSize(),
                                        FileType.PROFILE_IMAGE
                                )
                        )
                );

        try {
            return resolveProfileImageFileId(results);
        } catch (RuntimeException exception) {
            deleteRegistrationResultsSafely(
                    results,
                    exception
            );

            throw exception;
        }
    }

    // 프로필 이미지 등록 결과 검증
    private Long resolveProfileImageFileId(
            List<FileRegistrationResult> results
    ) {
        if (results == null || results.size() != 1) {
            throw new IllegalStateException(
                    "프로필 이미지 파일 등록 결과가 올바르지 않습니다."
            );
        }

        FileRegistrationResult result = results.get(0);

        if (result.fileType() != FileType.PROFILE_IMAGE) {
            throw new IllegalStateException(
                    "프로필 이미지 파일 등록 타입이 일치하지 않습니다."
            );
        }

        if (result.fileId() == null) {
            throw new IllegalStateException(
                    "프로필 이미지 파일 ID가 존재하지 않습니다."
            );
        }

        return result.fileId();
    }

    // 파일 등록 실패시 삭제 처리
    private void deleteRegistrationResultsSafely(
            List<FileRegistrationResult> results,
            RuntimeException originalException
    ) {
        if (results == null) {
            return;
        }

        for (FileRegistrationResult result : results) {
            if (result == null || result.fileId() == null) {
                continue;
            }

            deleteFileSafely(
                    result.fileId(),
                    originalException
            );
        }
    }
    // 파일 삭제 요청
    private void deleteFileSafely(
            Long fileId,
            RuntimeException originalException
    ) {
        if (fileId == null) {
            return;
        }

        try {
            fileUseCase.deleteFile(fileId);
        } catch (RuntimeException cleanupException) {
            originalException.addSuppressed(cleanupException);

            log.error(
                    "event=trainer_profile_file_cleanup_failed, fileId={}",
                    fileId,
                    cleanupException
            );
        }
    }
}
