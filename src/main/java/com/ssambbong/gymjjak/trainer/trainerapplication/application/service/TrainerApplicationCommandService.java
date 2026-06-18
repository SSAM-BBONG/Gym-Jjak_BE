package com.ssambbong.gymjjak.trainer.trainerapplication.application.service;

import com.ssambbong.gymjjak.file.application.result.FileContentResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.application.usecase.OcrUseCase;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.ApproveTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateApprovedTrainerProfileCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.UpdateTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.ApprovedTrainerProfilePort;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out.TrainerApplicationUserPort;
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
        log.info(
                "event=trainer_application_create_started, applicantUserId={}, profileImageFileId={}, certificateFileId={}",
                command.applicantUserId(),
                command.profileImageFileId(),
                command.certificateFileId()
        );
        // 필수값 검증
        validateRequiredCommand(command);
        // 중복 신청 검증
        validateDuplicateApplication(command.applicantUserId());
        // 파일 다운로드 호출
        FileContentResult certificateFile = downloadCertificateFile(command);
        // ocr 검증
        OcrResult ocrResult =extractCertificateOcr(certificateFile);
        // ocr 요구 반환값 검증
        validateRequiredCertification(command, ocrResult);

        return transactionTemplate.execute(status -> saveTrainerApplication(command));
    }

    private Long saveTrainerApplication(CreateTrainerApplicationCommand command) {
        // 중복 신청 검사 : TOCTOU 방지를 위해 저장 직전에 중복 검증을 한 번 더 함
        validateDuplicateApplication(command.applicantUserId());

        TrainerApplication trainerApplication = TrainerApplication.create(
                command.applicantUserId(),
                command.profileImageFileId(),
                command.certificateFileId(),
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
        log.info(
                "event=trainer_application_approve_started, trainerApplicationId={}, adminId={}",
                command.trainerApplicationId(),
                command.adminId()
        );
        // approve command 검증
        validateApproveCommand(command);

        // 심사받는 user 존재 여부 확인
        TrainerApplication trainerApplication = trainerApplicationRepository.findById(command.trainerApplicationId())
                .orElseThrow(() -> new TrainerApplicationNotFoundException(command.trainerApplicationId()));

        // 대기 상태 검증
        validatePendingStatus(trainerApplication);

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

        // 승인된 user 처리
        TrainerApplication approvedTrainerApplication =
                trainerApplication.approve(command.adminId(), LocalDateTime.now());

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

    private void validateApproveCommand(ApproveTrainerApplicationCommand command) {
        if (command == null) {
            throw new InvalidTrainerApplicationException("command는 필수입니다.");
        }

        if (command.trainerApplicationId() == null) {
            throw new InvalidTrainerApplicationException("trainerApplicationId는 필수입니다.");
        }

        if (command.adminId() == null) {
            throw new InvalidTrainerApplicationException("adminId는 필수입니다.");
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

        if (command.certificateFileId() == null) {
            throw new InvalidTrainerApplicationException("certificateFileId는 필수입니다.");
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

    // OCR에 업로드할 파일 다운로드
    private FileContentResult downloadCertificateFile(CreateTrainerApplicationCommand command) {
        log.info(
                "event=trainer_application_certificate_download_started, applicantUserId={}, certificateFileId={}",
                command.applicantUserId(),
                command.certificateFileId()
        );

        FileContentResult certificateFile = fileUseCase.downloadFile(
                command.certificateFileId(),
                command.applicantUserId(),
                false,
                FileType.CERTIFICATION
        );

        log.info(
                "event=trainer_application_certificate_download_succeeded, applicantUserId={}, certificateFileId={}, contentType={}, fileSize={}",
                command.applicantUserId(),
                command.certificateFileId(),
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

    private void validateRequiredCertification(CreateTrainerApplicationCommand command, OcrResult ocrResult) {

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
                    "event=trainer_application_required_certification_not_verified, applicantUserId={}, certificateFileId={}, templateName={}, validTemplate={}, hasGrade={}, hasEvent={}, hasAcquiredDate={}",
                    command.applicantUserId(),
                    command.certificateFileId(),
                    ocrResult.matchedTemplateName(),
                    validTemplate,
                    hasGrade,
                    hasEvent,
                    hasAcquiredDate
            );

            throw new RequiredCertificationNotVerifiedException(
                    command.applicantUserId(),
                    command.certificateFileId()
            );
        }

        log.info(
                "event=trainer_application_required_certification_verified, applicantUserId={}, certificateFileId={}, templateName={}, grade={}, event={}, acquiredDate={}",
                command.applicantUserId(),
                command.certificateFileId(),
                ocrResult.matchedTemplateName(),
                ocrResult.findTextByName(CERTIFICATION_GRADE_FIELD_NAME).orElse(null), // 운영시 -> hasGrade
                ocrResult.findTextByName(CERTIFICATION_EVENT_FIELD_NAME).orElse(null), // 운영시 -> hasEvent
                ocrResult.findTextByName(CERTIFICATION_ACQUIRED_DATE_FIELD_NAME).orElse(null) // 운영시 -> hasAcquiredDate
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
}
