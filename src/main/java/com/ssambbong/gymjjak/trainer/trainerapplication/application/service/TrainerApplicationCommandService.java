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
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.ApprovedTrainerProfilePort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationUserPort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.result.RegisteredTrainerApplicationFiles;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.result.TrainerApprovalUserInfo;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationCommandUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.*;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository.TrainerApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ApprovedTrainerProfilePort approvedTrainerProfilePort;

    @Override
    public Long createTrainerApplication(CreateTrainerApplicationCommand command) {

        // 필수값 검증
        validateRequiredCommand(command);
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

            // ocr 검증
            OcrResult ocrResult =extractCertificateOcr(certificateFile);

            // ocr 요구 반환값 검증
            validateRequiredCertification(
                    command.applicantUserId(),
                    registeredFiles.certificateFileId(),
                    ocrResult
            );

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

        TrainerApplication trainerApplication = TrainerApplication.create(
                command.applicantUserId(),
                registeredFiles.profileImageFileId(),
                registeredFiles.certificateFileId(),
                command.qualifications(),
                command.awardHistories(),
                command.introduction()
        );

        // DB에 저장
        TrainerApplication savedTrainerApplication =
                trainerApplicationRepository.save(trainerApplication);

        log.info(
                "event=trainer_application_create_succeeded, trainerApplicationId={}, applicantUserId={}",
                savedTrainerApplication.getTrainerApplicationId(),
                savedTrainerApplication.getUserId()
        );

        return savedTrainerApplication.getTrainerApplicationId();
    }

    @Override
    @Transactional
    public Long updateTrainerApplication(UpdateTrainerApplicationCommand command) {

        log.info(
                "event=trainer_application_update_started, trainerApplicationId={}, requesterId={},",
                command.trainerApplicationId(),
                command.requesterId()
        );
        // 필수값 검증
        validateUpdateCommand(command);

        TrainerApplication trainerApplication = trainerApplicationRepository.findById(command.trainerApplicationId())
                .orElseThrow(() -> new TrainerApplicationNotFoundException(command.trainerApplicationId()));

        // 본인 검증
        validateUpdatePermission(trainerApplication, command.requesterId());

        // 대기 상태 검증
        validatePendingStatus(trainerApplication);

        TrainerApplication updatedTrainerApplication = trainerApplication.updateApplication(
                command.profileImageFileId(),
                command.qualifications(),
                command.awardHistories(),
                command.introduction()
        );

        TrainerApplication savedTrainerApplication =
                trainerApplicationRepository.save(updatedTrainerApplication);

        log.info(
                "event=trainer_application_update_succeeded, trainerApplicationId={}, requesterId={}",
                savedTrainerApplication.getTrainerApplicationId(),
                command.requesterId()
        );

        return savedTrainerApplication.getTrainerApplicationId();
    }

    @Override
    @Transactional
    public Long approveTrainerApplication(ApproveTrainerApplicationCommand command) {

        // approve command 검증
        validateApproveCommand(command);

        log.info(
                "event=trainer_application_approve_started, trainerApplicationId={}, adminId={}",
                command.trainerApplicationId(),
                command.adminId()
        );

        // 심사받는 user 존재 여부 확인
        TrainerApplication trainerApplication =
                trainerApplicationRepository.findByIdForUpdate(command.trainerApplicationId())
                .orElseThrow(() -> new TrainerApplicationNotFoundException(command.trainerApplicationId()));

        // 승인된 user 처리
        TrainerApplication approvedTrainerApplication =
                trainerApplication.approve(command.adminId(), LocalDateTime.now());

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

        // 승인된 user 값 update
        trainerApplicationRepository.save(approvedTrainerApplication);

        log.info(
                "event=trainer_application_approve_succeeded, trainerApplicationId={}, userId={}, trainerProfileId={}, adminId={}",
                approvedTrainerApplication.getTrainerApplicationId(),
                approvedTrainerApplication.getUserId(),
                trainerProfileId,
                command.adminId()
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

        log.info(
                "event=trainer_application_reject_started, " +
                        "trainerApplicationId={}, adminId={}",
                command.trainerApplicationId(), command.adminId()
        );

        TrainerApplication trainerApplication =
                trainerApplicationRepository.findByIdForUpdate(
                        command.trainerApplicationId()
                ).orElseThrow(() ->
                        new TrainerApplicationNotFoundException(
                                command.trainerApplicationId()
                        )
                );

        TrainerApplication rejectTrainerApplication =
                trainerApplication.reject(
                        command.adminId(),
                        command.rejectReason(),
                        LocalDateTime.now()
                );

        trainerApplicationRepository.save(rejectTrainerApplication);

        log.info(
                "event=trainer_application_reject_succeeded, " +
                        "trainerApplicationId={}, applicantUserId={}, adminId={}",
                rejectTrainerApplication.getTrainerApplicationId(),
                rejectTrainerApplication.getUserId(),
                command.adminId()
        );
    }

    private void validateApproveCommand(ApproveTrainerApplicationCommand command) {
        if (command == null) {
            throw new InvalidTrainerApplicationException("command는 필수입니다.");
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

        if (command.introduction() == null || command.introduction().isBlank()) {
            throw new InvalidTrainerApplicationException("introduction은 필수입니다.");
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
        // 담긴 파일들 등록 메서드 거쳐서 결과로 담기
        List<FileRegistrationResult> results =
                fileUseCase.registerFiles(fileCommands);

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
