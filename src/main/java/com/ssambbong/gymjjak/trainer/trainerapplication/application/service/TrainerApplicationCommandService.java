package com.ssambbong.gymjjak.trainer.trainerapplication.application.service;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainerApplicationCommandService implements TrainerApplicationCommandUseCase {


    @Override
    public Long createTrainerApplication(CreateTrainerApplicationCommand command) {
        log.info(
                "event=trainer_application_create_started, applicantUserId={}, profileImageFileId={}, certificateFileId={}",
                command.applicantUserId(),
                command.profileImageFileId(),
                command.certificateFileId()
        );

        /* TODO
        *   File 도메인에서 fileId으로 S3 bytes 조회 기능이 추가 되면 아래 기능들 추가 예정
        *
        *   1. certificateFileId로 file 도메인에서 파일 byte 요청하기
        *   - expectedFileType: CERTIFICATION
        *   - requesterId: applicantUserId
        *
        *   2. 반환받은 bytes를 OCR 공통 패키지로 전달
        *   - originalFilename, contentType, fileBytes
        *
        *   3. OCR 결과에서 필수 자격증인 생활스포츠지도사 여부 검증
        *
        *   4. profileImageFileId 검증
        *   - expectedFileType: PROFILE_IMAGE
        *
        *   5. TrainerApplication 도메인 생성
        *
        *   6. TrainerApplicationRepository 저장
        * */

        // TODO : 추후에 리포 저장하는걸로 수정 예정
        throw new UnsupportedOperationException("트레이너 신청 저장 로직 구현이 필요합니다.");
    }
}
