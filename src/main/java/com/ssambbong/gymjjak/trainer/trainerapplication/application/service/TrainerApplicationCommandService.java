package com.ssambbong.gymjjak.trainer.trainerapplication.application.service;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.result.FileContentResult;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.application.usecase.OcrUseCase;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.*;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.event.TrainerApplicationApprovedEvent;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.event.TrainerApplicationRejectedEvent;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.metrics.TrainerApplicationTimed;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.ApprovedTrainerProfilePort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationOrganizationPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationOrganizationTrainerPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationUserPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.result.RegisteredTrainerApplicationFiles;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.result.TrainerApprovalUserInfo;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationCommandUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.*;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository.TrainerApplicationRepository;
import com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.metrics.TrainerApplicationMetric;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.ProfileImageUpdateAction;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerApplicationCommandService implements TrainerApplicationCommandUseCase {

    private static final String REQUIRED_CERTIFICATION_TEMPLATE_NAME = "생활스포츠지도사";
    private static final String CERTIFICATION_GRADE_FIELD_NAME = "자격등급";
    private static final String CERTIFICATION_EVENT_FIELD_NAME = "자격종목";
    private static final String CERTIFICATION_ACQUIRED_DATE_FIELD_NAME = "자격취득일";

    private final FileUseCase fileUseCase;
    private final OcrUseCase ocrUseCase;
    private final TrainerApplicationRepository trainerApplicationRepository;
    private final TransactionTemplate transactionTemplate;
    private final TrainerApplicationUserPort trainerApplicationUserPort;
    private final TrainerApplicationOrganizationPort trainerApplicationOrganizationPort;
    private final ApprovedTrainerProfilePort approvedTrainerProfilePort;
    // 헬스장 트레이너 테이블로 쏘는 Port 추가
    private final TrainerApplicationOrganizationTrainerPort trainerApplicationOrganizationTrainerPort;

    private final ApplicationEventPublisher eventPublisher;
    // 메트릭 추가
    private final TrainerApplicationMetric trainerApplicationMetric;

    @TrainerApplicationTimed(operation = "create")
    @Override
    public Long createTrainerApplication(CreateTrainerApplicationCommand command) {

        // 필수값 검증
        validateRequiredCommand(command);
        // 신청 대상 조직 List 검증
        validateReceivableOrganizations(command.organizationIds());
        // 중복 신청 검증
        validateDuplicateApplication(command.applicantUserId());

        log.info(
                "event=trainer_application_create_started, applicantUserId={}, profileImageFilePresent={}, certificateFilePresent={}",
                command.applicantUserId(),
                command.profileImageFile() != null,
                command.certificateFile() != null
        );

        // 파일 등록 메서드 (반환값 FileId들)
        RegisteredTrainerApplicationFiles registeredFiles =
                registerTrainerApplicationFiles(command);

        try {
            // 파일 다운로드 호출
            FileContentResult certificateFile = downloadCertificateFile(
                    registeredFiles.certificateFileId(),
                    command.applicantUserId()
            );

            // ocr 객체 생성
            OcrResult ocrResult;

            // ocr duration 타이머 측정
            Timer.Sample ocrValidationTimer =
                    trainerApplicationMetric.startTimer();

            String ocrOutcome = trainerApplicationMetric.success();

            try {
                // ocr 파일 검증
                ocrResult = extractCertificateOcr(certificateFile);

                // ocr 요구 반환값 검증
                validateRequiredCertification(
                        command.applicantUserId(),
                        registeredFiles.certificateFileId(),
                        ocrResult
                );
            } catch (RuntimeException exception) {
                ocrOutcome = trainerApplicationMetric.failure();
                throw exception;
            } finally {
                // Duration 측정 끝
                trainerApplicationMetric.recordOcrValidationDurationSafely(
                        ocrValidationTimer,
                        ocrOutcome
                );
            }

            return transactionTemplate.execute(status ->
                    saveTrainerApplication(command, registeredFiles)
            );
        } catch (RuntimeException exception) {
            deleteRegisteredFilesSafely(registeredFiles, exception);
            throw exception;
        }
    }

    private Long saveTrainerApplication(
            CreateTrainerApplicationCommand command,
            RegisteredTrainerApplicationFiles registeredFiles
    ) {
        // 중복 신청 검사 : TOCTOU 방지를 위해 저장 직전에 중복 검증을 한 번 더 함
        validateDuplicateApplication(command.applicantUserId());

        List<TrainerApplication> trainerApplications =
                command.organizationIds().stream()
                        .map(organizationId -> TrainerApplication.create(
                                command.applicantUserId(),
                                organizationId,
                                registeredFiles.profileImageFileId(),
                                registeredFiles.certificateFileId(),
                                command.qualifications(),
                                command.awardHistories(),
                                command.introduction()
                        ))
                        .toList();

        // 메트릭 추가
        Timer.Sample dbSaveTimer =
                trainerApplicationMetric.startTimer();

        String outcome = trainerApplicationMetric.success();

        try {
            // DB에 저장
            List<TrainerApplication> savedTrainerApplications =
                    trainerApplicationRepository.saveAll(trainerApplications);

            log.info(
                    "event=trainer_applications_create_succeeded, applicantUserId={}, organizationCount={}, trainerApplicationIds={}",
                    command.applicantUserId(),
                    savedTrainerApplications.size(),
                    savedTrainerApplications.stream()
                            .map(TrainerApplication::getTrainerApplicationId)
                            .toList()
            );

            // 응답 DTO를 다건 ID로 바꾸기 전까지의 임시 호환 반환값
            return savedTrainerApplications.get(0).getTrainerApplicationId();

        } catch (RuntimeException exception) {
            outcome = trainerApplicationMetric.failure();
            throw exception;
        } finally {
            trainerApplicationMetric.recordDbSaveDurationSafely(
                    dbSaveTimer,
                    "create",
                    outcome
            );
        }
    }

    @Override
    public Long updateTrainerApplication(UpdateTrainerApplicationCommand command) {

        // 필수값 검증
        validateUpdateCommand(command);

        log.info(
                "event=trainer_application_update_started, trainerApplicationId={}, requesterId={},",
                command.trainerApplicationId(),
                command.requesterId()
        );

        // 프로필이미지 수정 검증
        validateProfileImageUpdate(
                command.profileImageAction(),
                command.profileImageFile()
        );

        TrainerApplication trainerApplication = trainerApplicationRepository.findById(command.trainerApplicationId())
                .orElseThrow(() -> new TrainerApplicationNotFoundException(command.trainerApplicationId()));

        // 본인 검증
        validateUpdatePermission(trainerApplication, command.requesterId());

        // 대기 상태 검증
        validatePendingStatus(trainerApplication);

        // 수정 전 이미지
        Long oldProfileFileId = trainerApplication.getProfileFileId();

        // 신규 이미지 파일 등록 후, FileID 추출
        Long newProfileFileId = registerNewTrainerApplicationProfileImage(command);

        // 최종 이미지 파일 ID
        Long updatedProfileFileId =
                resolveUpdatedProfileFileId(
                        command.profileImageAction(),
                        oldProfileFileId,
                        newProfileFileId
                );

        // 자기소개 수정
        String updatedIntroduction =
                command.introduction() == null
                        ? trainerApplication.getIntroduction()
                        : command.introduction();

        // 수정된 트레이너 신청서 ID
        Long updatedTrainerApplicationId;

        try {
            updatedTrainerApplicationId
                    = transactionTemplate.execute(status ->
                    updateTrainerApplicationInTransaction(
                            trainerApplication,
                            updatedProfileFileId,
                            updatedIntroduction,
                            command
                    )
            );

            if (updatedTrainerApplicationId == null) {
                throw new IllegalStateException(
                        "트레이너 신청서 수정 결과가 존재하지 않습니다."
                );
            }
        } catch (RuntimeException e) {
            // 새로운 파일 삭제 요청
            deleteFileSafely(
                    newProfileFileId,
                    e
            );
            throw e;
        }

        // 이전 파일 삭제 실패시, 에러 로그만 출력
        deleteOldProfileImageSafely(
                command.profileImageAction(),
                oldProfileFileId,
                newProfileFileId
        );

        log.info(
                "event=trainer_application_update_succeeded, trainerApplicationId={}, requesterId={}, profileImageAction={}",
                updatedTrainerApplicationId,
                command.requesterId(),
                command.profileImageAction()
        );

        return updatedTrainerApplicationId;
    }

    private void deleteOldProfileImageSafely(
            ProfileImageUpdateAction action,
            Long oldProfileFileId,
            Long newProfileFileId
    ) {
        // 이전, 이후 둘다 이미지 X
        if (action == ProfileImageUpdateAction.KEEP || oldProfileFileId == null) {
            return;
        }
        // 기존 파일 유지 시, return
        if (oldProfileFileId.equals(newProfileFileId)) {
            return;
        }

        try {
            fileUseCase.deleteFile(oldProfileFileId);
        } catch (RuntimeException exception) {
            log.error(
                    "event=trainer_application_old_profile_file_cleanup_failed, " +
                            "oldProfileFileId={}, imageAction={}",
                    oldProfileFileId,
                    action,
                    exception
            );
        }
    }

    // 트레이너 신청서 DB 수정 전용 메서드
    private Long updateTrainerApplicationInTransaction(
            TrainerApplication trainerApplication,
            Long updatedProfileFileId,
            String updatedIntroduction,
            UpdateTrainerApplicationCommand command
    ) {
        // 트레이너 신청서 최신화
        TrainerApplication updatedTrainerApplication =
                trainerApplication.updateApplication(
                        updatedProfileFileId,
                        command.qualifications(),
                        command.awardHistories(),
                        updatedIntroduction
                );

        TrainerApplication savedTrainerApplication =
                trainerApplicationRepository.save(updatedTrainerApplication);

        return savedTrainerApplication.getTrainerApplicationId();
    }

    // profileFileId 결정 메서드
    private Long resolveUpdatedProfileFileId(
            ProfileImageUpdateAction action,
            Long currentProfileFileId,
            Long newProfileImageFileId) {
        return switch (action) {
            case KEEP -> currentProfileFileId;
            case REPLACE ->  newProfileImageFileId;
            case DELETE -> null;
        };

    }

    // 새로운 프로필 이미지 등록
    private Long registerNewTrainerApplicationProfileImage(UpdateTrainerApplicationCommand command) {
        // 변경 아니면 바로 return
        if (command.profileImageAction() != ProfileImageUpdateAction.REPLACE) {
            return null;
        }

        // 업로드된 Meta 데이터 추출
        UploadedFileMetadataCommand file = command.profileImageFile();

        // 파일 id, 타입 객체 List 추출
        List<FileRegistrationResult> results =
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
            // FileID 반환
            return resolveProfileImageFileId(results);
        } catch (RuntimeException exception) {
            deleteRegistrationResultsSafely(results, exception);
            throw exception;
        }

    }

    private Long resolveProfileImageFileId(List<FileRegistrationResult> results) {
        if (results == null || results.size() != 1) {
            throw new IllegalStateException(
                    "프로필 이미지 파일 등록 결과가 올바르지 않습니다."
            );
        }
        // 첫번째 값 추출
        FileRegistrationResult result = results.get(0);

        if (result.fileType() != FileType.PROFILE_IMAGE) {
            throw new IllegalStateException(
                    "프로필 이미지 파일 등록 결과 타입이 일치하지 않습니다."
            );
        }

        if (result.fileId() == null) {
            throw new IllegalStateException(
                    "프로필 이미지 파일 ID가 존재하지 않습니다."
            );
        }

        return result.fileId();
    }

    // 프로필이미지 수정 검증
    private void validateProfileImageUpdate(
            ProfileImageUpdateAction action, UploadedFileMetadataCommand file) {

        if (action == null) {
            throw new InvalidTrainerApplicationException(
                    "프로필 이미지 수정 방식은 필수입니다."
            );
        }

        if (action == ProfileImageUpdateAction.REPLACE && file == null) {
            throw new InvalidTrainerApplicationException(
                    "프로필 이미지를 교체하려면 파일 정보가 필요합니다."
            );
        }

        if (action != ProfileImageUpdateAction.REPLACE && file != null) {
            throw new InvalidTrainerApplicationException(
                    "프로필 이미지 파일 정보는 REPLACE 요청에서만 전달할 수 있습니다."
            );
        }

    }

    @Override
    @Transactional
    public Long approveTrainerApplication(ApproveTrainerApplicationCommand command) {

        // approve command 검증
        validateApproveCommand(command);

        // 조직 userId로 조직Id 추출
        Long organizationId =
                trainerApplicationOrganizationPort.findOrganizationIdByAccountId(
                        command.organizationAccountId()
                );

        log.info(
                "event=trainer_application_approve_started, trainerApplicationId={}, organizationAccountId={}, organizationId={}",
                command.trainerApplicationId(),
                command.organizationAccountId(),
                organizationId
        );

        // 심사받는 user 존재 여부 확인
        TrainerApplication trainerApplication =
                trainerApplicationRepository.findByIdForUpdate(command.trainerApplicationId())
                .orElseThrow(() -> new TrainerApplicationNotFoundException(command.trainerApplicationId()));

        // 조직 검토 권한 검증
        trainerApplication.validateReviewableBy(organizationId);

        // 승인된 user 처리
        TrainerApplication approvedTrainerApplication =
                trainerApplication.approve(
                        command.organizationAccountId(),
                        LocalDateTime.now()
                );

        // Users 도메인으로 role값 변경 port 요청
        TrainerApprovalUserInfo userInfo =
                trainerApplicationUserPort.promoteToTrainer(trainerApplication.getUserId());

        // 트레이너 프로필 테이블 insert port 요청
        Long trainerProfileId = approvedTrainerProfilePort.createApprovedTrainerProfile(
                new CreateApprovedTrainerProfileCommand(
                        trainerApplication.getUserId(),
                        trainerApplication.getTrainerApplicationId(),
                        trainerApplication.getProfileFileId(),
                        userInfo.name(),
                        trainerApplication.getIntroduction(),
                        trainerApplication.getQualifications(),
                        trainerApplication.getCertificateFileId(),
                        trainerApplication.getAwardHistories()
                )
        );

        // 조직 트레이너 테이블에 저장해달라고 하는 Port 추가
        trainerApplicationOrganizationTrainerPort.registerApprovedTrainer(
                organizationId,
                trainerProfileId,
                command.organizationAccountId()
        );

        // 승인된 user 값 update
        trainerApplicationRepository.save(approvedTrainerApplication);

        // 트레이너 신청 승인 이벤트 발행
        eventPublisher.publishEvent(
                new TrainerApplicationApprovedEvent(
                        approvedTrainerApplication.getUserId(),
                        approvedTrainerApplication.getTrainerApplicationId(),
                        trainerProfileId
                )
        );

        log.info(
                "event=trainer_application_approve_succeeded, trainerApplicationId={}, userId={}, trainerProfileId={}, organizationAccountId={}, organizationId={}",
                approvedTrainerApplication.getTrainerApplicationId(),
                approvedTrainerApplication.getUserId(),
                trainerProfileId,
                command.organizationAccountId(),
                organizationId
        );

        return trainerProfileId;
    }

    @Override
    @Transactional
    public void rejectTrainerApplication(RejectTrainerApplicationCommand command) {

        // command 값 검증
        if (command == null) {
            throw new InvalidTrainerApplicationException(
                    "command 값은 필수입니다."
            );
        }

        Long organizationId =
                trainerApplicationOrganizationPort.findOrganizationIdByAccountId(
                        command.organizationAccountId()
                );

        log.info(
                "event=trainer_application_reject_started, trainerApplicationId={}, organizationAccountId={}, organizationId={}",
                command.trainerApplicationId(),
                command.organizationAccountId(),
                organizationId
        );

        TrainerApplication trainerApplication =
                trainerApplicationRepository.findByIdForUpdate(
                        command.trainerApplicationId()
                ).orElseThrow(() ->
                        new TrainerApplicationNotFoundException(
                                command.trainerApplicationId()
                        )
                );

        trainerApplication.validateReviewableBy(organizationId);

        TrainerApplication rejectTrainerApplication =
                trainerApplication.reject(
                        command.organizationAccountId(),
                        command.rejectReason(),
                        LocalDateTime.now()
                );

        trainerApplicationRepository.save(rejectTrainerApplication);

        // 트레이너 신청 반려 이벤트
        eventPublisher.publishEvent(
                new TrainerApplicationRejectedEvent(
                        rejectTrainerApplication.getUserId(),
                        rejectTrainerApplication.getTrainerApplicationId(),
                        command.rejectReason()
                )
        );

        log.info(
                "event=trainer_application_reject_succeeded, trainerApplicationId={}, applicantUserId={}, organizationAccountId={}, organizationId={}",
                rejectTrainerApplication.getTrainerApplicationId(),
                rejectTrainerApplication.getUserId(),
                command.organizationAccountId(),
                organizationId
        );
    }

    @Override
    public void cancelTrainerApplication(CancelTrainerApplicationCommand command) {

        if (command == null) {
            throw new InvalidTrainerApplicationException(
                    "command 값은 필수입니다."
            );
        }

        log.info(
                "event=trainer_application_cancel_start," +
                        "trainerApplicationId={}, requesterId={}",
                command.trainerApplicationId(), command.requesterId()
        );

        TrainerApplication deletedApplication =
                // 신청서 삭제만 트랜잭션 걸기 조회 후, 신청서 삭제
                transactionTemplate.execute(status -> {
                    TrainerApplication trainerApplication =
                            trainerApplicationRepository.findByIdForUpdate(
                                    command.trainerApplicationId()
                            ).orElseThrow(() ->
                                    new TrainerApplicationNotFoundException(
                                            command.trainerApplicationId()
                                    )
                            );

                    // 취소 가능 상태 검증
                    trainerApplication.validateCancel(
                            command.requesterId()
                    );

                    // 삭제 요청
                   trainerApplicationRepository.deleteById(
                           command.trainerApplicationId()
                   );

                   return trainerApplication;
                });

        if (deletedApplication == null) {
            throw new IllegalStateException(
                    "트레이너 신청 취소 트랜잭션 결과가 없습니다."
            );
        }

        // 이후 파일 삭제
        deleteCanceledApplicationFilesSafely(
                deletedApplication
        );

        log.info(
                "event=trainer_application_cancel_succeeded, " +
                        "trainerApplicationId={}, requesterId={}",
                command.trainerApplicationId(),
                command.requesterId()
        );
    }

    private void deleteCanceledApplicationFilesSafely(TrainerApplication deletedApplication) {

        deleteCanceledApplicationFileSafely(
                deletedApplication.getProfileFileId(),
                deletedApplication.getTrainerApplicationId()
        );

        deleteCanceledApplicationFileSafely(
                deletedApplication.getCertificateFileId(),
                deletedApplication.getTrainerApplicationId()
        );
    }

    private void deleteCanceledApplicationFileSafely(Long fileId, Long trainerApplicationId) {
        if (fileId == null) {
            return;
        }

        try {
            fileUseCase.deleteFile(fileId);
        } catch (RuntimeException exception) {
            log.error(
                    "event=trainer_application_cancel_file_cleanup_failed, " +
                            "trainerApplicationId={}, fileID={}",
                    trainerApplicationId, fileId, exception
            );
        }
    }


    // ===== 신청서 승인 =====
    private void validateApproveCommand(ApproveTrainerApplicationCommand command) {
        if (command == null) {
            throw new InvalidTrainerApplicationException("command는 필수입니다.");
        }

        if (command.trainerApplicationId() == null || command.trainerApplicationId() <= 0) {
            throw new InvalidTrainerApplicationException(
                    "trainerApplicationId는 1 이상이어야 합니다."
            );
        }

        if (command.organizationAccountId() == null || command.organizationAccountId() <= 0) {
            throw new InvalidTrainerApplicationException(
                    "organizationAccountId는 1 이상이어야 합니다."
            );
        }
    }


    // ====== 트레이너 신청서 변경 ======
    private void validateUpdateCommand(UpdateTrainerApplicationCommand command) {

        if (command == null) {
            throw new InvalidTrainerApplicationException("command는 필수입니다.");
        }

        if (command.trainerApplicationId() == null) {
            throw new InvalidTrainerApplicationException("trainerApplicationId는 필수입니다.");
        }

        if (command.requesterId() == null) {
            throw new InvalidTrainerApplicationException("requesterId는 필수입니다.");
        }

        if (command.introduction() != null && command.introduction().isBlank()) {
            throw new InvalidTrainerApplicationException(
                    "introduction은 공백일 수 없습니다."
            );
        }
    }

    private void validateUpdatePermission(
            TrainerApplication trainerApplication,
            Long requesterId
    ) {
        if (!trainerApplication.isOwner(requesterId)) {
            log.warn(
                    "event=trainer_application_update_denied, trainerApplicationId={}, ownerId={}, requesterId={}",
                    trainerApplication.getTrainerApplicationId(),
                    trainerApplication.getUserId(),
                    requesterId
            );

            throw new ForbiddenTrainerApplicationUpdateException(
                    requesterId,
                    trainerApplication.getTrainerApplicationId()
            );
        }
    }

    private void validatePendingStatus(TrainerApplication trainerApplication) {
        if (!trainerApplication.isPending()) {
            log.warn(
                    "event=trainer_application_update_not_pending, trainerApplicationId={}, status={}",
                    trainerApplication.getTrainerApplicationId(),
                    trainerApplication.getStatus()
            );

            throw new TrainerApplicationStatusConflictException(
                    trainerApplication.getTrainerApplicationId(),
                    trainerApplication.getStatus()
            );
        }
    }


    // ============ 트레이너 신청 =================
    // 필수값 검증
    private void validateRequiredCommand(CreateTrainerApplicationCommand command) {

        if (command == null) {
            throw new InvalidTrainerApplicationException("command는 필수입니다.");
        }

        if (command.applicantUserId() == null) {
            throw new InvalidTrainerApplicationException("applicantUserId는 필수입니다.");
        }

        if (command.certificateFile() == null) {
            throw new InvalidTrainerApplicationException("certificateFile은 필수입니다.");
        }

        if (command.introduction() == null || command.introduction().isBlank()) {
            throw new InvalidTrainerApplicationException("introduction은 필수입니다.");
        }
    }

    // 신청 조직 List -> 단일로 변환
    private void validateReceivableOrganizations(
            List<Long> organizationIds
    ) {
        for (Long organizationId : organizationIds) {
            validateReceivableOrganization(organizationId);
        }
    }

    // 신청 대상 조직 검증 기능
    private void validateReceivableOrganization(Long organizationId) {
        if (!trainerApplicationOrganizationPort.existsActiveOrganizationById(organizationId)) {
            throw new InvalidTrainerApplicationException(
                    "신청 가능한 조직이 존재하지 않습니다."
            );
        }
    }

    // 중복 신청 검증
    private void validateDuplicateApplication(Long applicantUserId) {
        boolean exists = trainerApplicationRepository.existsDuplicateBlockingApplicationByUserId(applicantUserId);

        if (exists) {
            log.warn(
                    "event=trainer_application_duplicate_detected, applicantUserId={}",
                    applicantUserId
            );

            throw new DuplicateTrainerApplicationException(applicantUserId);
        }
    }

    // 파일 등록 메서드
    private RegisteredTrainerApplicationFiles registerTrainerApplicationFiles(
            CreateTrainerApplicationCommand command
    ) {
        // file 등록 도메인 List 만들기 => 이걸 한번에 File 도메인으로 보냄
        List<CreateFileCommand> fileCommands = new ArrayList<>();

        // 프로필 이미지 메타 데이터 추출
        UploadedFileMetadataCommand profileImageFile =
                command.profileImageFile();

        // 프로필 이미지 리스트에 추가
        if (profileImageFile != null) {
            fileCommands.add(
                    new CreateFileCommand(
                            command.applicantUserId(),
                            profileImageFile.fileKey(),
                            profileImageFile.originalName(),
                            profileImageFile.contentType(),
                            profileImageFile.fileSize(),
                            FileType.PROFILE_IMAGE
                    )
            );
        }

        // 필수 자격증 메타 데이터 추출
        UploadedFileMetadataCommand certificateFile =
                command.certificateFile();
        // 리스트 추가
        fileCommands.add(
                new CreateFileCommand(
                        command.applicantUserId(),
                        certificateFile.fileKey(),
                        certificateFile.originalName(),
                        certificateFile.contentType(),
                        certificateFile.fileSize(),
                        FileType.CERTIFICATION
                )
        );

        String fileGroup = profileImageFile == null
                ? "certification"
                : "profile_image_certification";

        // 메트릭 측정
        Timer.Sample fileRegisterTimer =
                trainerApplicationMetric.startTimer();

        String outcome = trainerApplicationMetric.success();

        // 파일 등록 list 생성
        List<FileRegistrationResult> results;

        try {
            // 담긴 파일들 등록 메서드 거쳐서 결과로 담기
            results = fileUseCase.registerFiles(fileCommands);
        } catch (RuntimeException exception) {
            outcome = trainerApplicationMetric.failure();
            throw exception;
        } finally {
            trainerApplicationMetric.recordFileRegisterDurationSafely(
                    fileRegisterTimer,
                    fileGroup,
                    outcome
            );
        }

        try {
            // 트레이너 신청에 필요한 id 값으로 변환해서 반환
            return resolveRegisteredFiles(
                    results,
                    profileImageFile != null);
        } catch (RuntimeException exception) {
            // 파일 결과 해석 실패 시, 보상 삭제
            deleteRegistrationResultsSafely(results, exception);
            throw exception;
        }

    }

    // 등록 메서드 실패시, 등록 메서드 내부에서 결과 목록 정리
    private void deleteRegistrationResultsSafely(
            List<FileRegistrationResult> results,
            RuntimeException exception) {
        // 등록 결과 포함된 모든 파일 id 순회하면서 삭제
        for (FileRegistrationResult result : results) {
            deleteFileSafely(
                    result.fileId(),
                    exception
            );
        }
    }

    // 파일등록 결과(fileId로 반환) 처리 메서드
    private RegisteredTrainerApplicationFiles resolveRegisteredFiles(
            List<FileRegistrationResult> results,
            boolean profileImageRequested
    ) {
        // 초기화
        Long profileImageFileId = null;
        Long certificateFileId = null;

        // 결과값 하나씩 꺼내서 해당 파일 id 추출
        for (FileRegistrationResult result : results) {
            if (result.fileType() == FileType.PROFILE_IMAGE) {
                profileImageFileId = result.fileId();
            }

            if (result.fileType() == FileType.CERTIFICATION) {
                certificateFileId = result.fileId();
            }
        }

        // 필수자격증 null 검증, TODO : 커스텀 예외처리 하기
        if (certificateFileId == null) {
            throw new IllegalStateException(
                    "자격증 파일 등록 결과가 존재하지 않습니다."
            );
        }

        // 프로필 이미지 파일 요청이 존재하지만, id로 null 값이 왔을 때
        if (profileImageRequested && profileImageFileId == null) {
            throw new IllegalStateException(
                    "프로필 이미지 파일 등록 결과가 존재하지 않습니다."
            );
        }

        return new RegisteredTrainerApplicationFiles(
                profileImageFileId,
                certificateFileId
        );
    }

    // OCR에 업로드할 파일 다운로드
    private FileContentResult downloadCertificateFile(
            Long certificateFileId,
            Long applicantUserId
    ) {
        log.info(
                "event=trainer_application_certificate_download_started, " +
                        "applicantUserId={}, certificateFileId={}",
                applicantUserId,
                certificateFileId
        );

        Timer.Sample certificateDownloadTimer =
                trainerApplicationMetric.startTimer();

        String outcome = trainerApplicationMetric.success();

        try {
            FileContentResult certificateFile = fileUseCase.downloadFile(
                    certificateFileId,
                    applicantUserId,
                    false,
                    FileType.CERTIFICATION
            );

            log.info(
                    "event=trainer_application_certificate_download_succeeded, " +
                            "applicantUserId={}, certificateFileId={}, " +
                            "contentType={}, fileSize={}",
                    applicantUserId,
                    certificateFileId,
                    certificateFile.contentType(),
                    certificateFile.fileSize()
            );

            return certificateFile;
        } catch (RuntimeException exception) {
            outcome = trainerApplicationMetric.failure();
            throw exception;
        } finally {
            trainerApplicationMetric.recordCertificateDownloadDurationSafely(
                    certificateDownloadTimer,
                    outcome
            );
        }

    }

    // ocr 변환 기능
    private OcrResult extractCertificateOcr(FileContentResult certificateFile) {

        log.info(
                "event=trainer_application_certificate_ocr_started, originalName={}, contentType={}. fileSize={}",
                certificateFile.originalName(),
                certificateFile.contentType(),
                certificateFile.fileSize()
        );

        // ocr 도메인 호출 하여 추출 필드값 받기
        OcrResult ocrResult = ocrUseCase.extractOcr(
                new ExtractOcrCommand(
                        certificateFile.originalName(),
                        certificateFile.contentType(),
                        certificateFile.bytes()
                )
        );

        log.info(
                "event=trainer_application_certificate_ocr_succeeded, templateName={}, fieldCount={}",
                ocrResult.matchedTemplateName(),
                ocrResult.fields().size()
        );

        return ocrResult;
    }

    private void validateRequiredCertification(
            Long applicantUserId,
            Long certificateFileId,
            OcrResult ocrResult
    ) {

        boolean validTemplate = isSameText(
                ocrResult.matchedTemplateName(),
                REQUIRED_CERTIFICATION_TEMPLATE_NAME
        );

        // ocr 반환값에서 각 필드값 존재 여부 확인
        boolean hasGrade = hasNotBlankField(ocrResult, CERTIFICATION_GRADE_FIELD_NAME);
        boolean hasEvent = hasNotBlankField(ocrResult, CERTIFICATION_EVENT_FIELD_NAME);
        boolean hasAcquiredDate = hasNotBlankField(ocrResult, CERTIFICATION_ACQUIRED_DATE_FIELD_NAME);

        boolean verified = validTemplate && hasGrade && hasEvent && hasAcquiredDate;

        if (!verified) {
            log.warn(
                    "event=trainer_application_required_certification_not_verified, " +
                            "applicantUserId={}, certificateFileId={}, templateName={}, " +
                            "validTemplate={}, hasGrade={}, hasEvent={}, hasAcquiredDate={}",
                    applicantUserId,
                    certificateFileId,
                    ocrResult.matchedTemplateName(),
                    validTemplate,
                    hasGrade,
                    hasEvent,
                    hasAcquiredDate
            );

            throw new RequiredCertificationNotVerifiedException(
                    applicantUserId,
                    certificateFileId
            );
        }

        log.info(
                "event=trainer_application_required_certification_verified, " +
                        "applicantUserId={}, certificateFileId={}, templateName={}",
                applicantUserId,
                certificateFileId,
                ocrResult.matchedTemplateName()
        );
    }

    // ocr 반환값 해당 필드값 존재 여부 확인
    private boolean hasNotBlankField(OcrResult ocrResult, String fieldName) {
        return ocrResult.findTextByName(fieldName) // 해당 필드명(inferText) 찾기
                .map(String::trim) // 앞 뒤 공백 제거
                .filter(text -> !text.isBlank()) // 빈 문자열 제거
                .isPresent(); // 존재하면 true
    }

    // 같은 문자열인지 비교
    private boolean isSameText(String actual, String expected) {
        if (actual == null || expected == null) {
            return false;
        }

        // 비교 전 공백 제거 후, 비교값 반환
        return normalizeText(actual).equals(normalizeText(expected));
    }

    // 문자열 속 모든 공백 제거
    private String normalizeText(String text) {
        return text.replaceAll("\\s+", "");
    }

    // 파일 업로드 실패 시, 헤딩 파일 삭제 요청
    private void deleteRegisteredFilesSafely(
            RegisteredTrainerApplicationFiles registeredFiles,
            RuntimeException originalException
    ) {
        deleteFileSafely(
                registeredFiles.profileImageFileId(),
                originalException
        );

        deleteFileSafely(
                registeredFiles.certificateFileId(),
                originalException
        );
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
                    "event=trainer_application_file_cleanup_failed, fileId={}",
                    fileId,
                    cleanupException
            );
        }
    }
}
