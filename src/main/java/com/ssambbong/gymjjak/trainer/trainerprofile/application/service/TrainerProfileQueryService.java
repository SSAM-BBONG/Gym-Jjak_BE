package com.ssambbong.gymjjak.trainer.trainerprofile.application.service;

import com.ssambbong.gymjjak.file.application.result.FileUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.file.exception.FileAccessDeniedException;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.MyTrainerProfileResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerAwardResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerCertificationResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase.TrainerProfileQueryUseCase;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerProfileQueryService implements TrainerProfileQueryUseCase {

    private final TrainerProfileRepository trainerProfileRepository;
    private final TrainerCertificationRepository trainerCertificationRepository;
    private final TrainerAwardRepository trainerAwardRepository;
    private final FileUrlUseCase fileUrlUseCase;

    @Override
    public MyTrainerProfileResult getMyTrainerProfile(Long requesterId) {
        log.info(
                "event=trainer_profile_my_detail_query_started, requesterId={}",
                requesterId
        );

        // 프로필 조회
        TrainerProfile profile =
                trainerProfileRepository.findByUserId(requesterId)
                        .orElseThrow(() ->
                                new TrainerProfileNotFoundException(requesterId)
                        );
        // 자격증 조회
        List<TrainerCertificationResult> certifications =
                trainerCertificationRepository
                        .findAllByTrainerProfileId(
                                profile.getTrainerProfileId()
                        )
                        .stream()
                        .map(certification ->
                                toCertificationResult(certification, requesterId)
                        )
                        .toList();
        // 수상경력 조회
        List<TrainerAwardResult> awards =
                trainerAwardRepository
                        .findAllByTrainerProfileId(
                                profile.getTrainerProfileId()
                        )
                        .stream()
                        .map(this::toAwardResult)
                        .toList();

        // 프로필 이미지 url
        FileUrlResult profileImageFile = resolveFile(
                profile.getProfileFileId(),
                requesterId
        );

        log.info(
                "event=trainer_profile_my_detail_query_succeeded, " +
                        "requesterId={}, trainerProfileId={}, " +
                        "certificationCount={}, awardCount={}",
                requesterId,
                profile.getTrainerProfileId(),
                certifications.size(),
                awards.size()
        );

        return new MyTrainerProfileResult(
                profile.getTrainerProfileId(),
                profileImageFile == null ? null : profileImageFile.url(),
                profileImageFile == null ? null : profileImageFile.originalName(),
                profile.getTrainerName(),
                profile.getIntroduction(),
                profile.getAverageRating(),
                profile.getReviewCount(),
                profile.getStatus(),
                certifications,
                awards
        );

    }

    // domain -> application 변환
    private TrainerAwardResult toAwardResult(TrainerAward award) {
        return new TrainerAwardResult(
                award.getTrainerAwardId(),
                award.getName()
        );
    }

    // domain -> application 변환
    private TrainerCertificationResult toCertificationResult(
            TrainerCertification certification, Long requesterId
    ) {

        FileUrlResult file = resolveFile(
                certification.getFileId(),
                requesterId
        );

        return new TrainerCertificationResult(
                certification.getTrainerCertificationId(),
                certification.getName(),
                certification.getCertificationType(),
                file == null ? null : file.url(),
                file == null ? null : file.originalName()
        );
    }

    // fileId -> fileURL
    private FileUrlResult resolveFile(Long fileId, Long requesterId) {
        if (fileId == null) {
            return null;
        }

        try {
            // TODO : 추후 originalFilename 받아오면, 반환타입 수정하기
            return fileUrlUseCase.getUrl(
                    fileId,
                    requesterId,
                    false
            );
        } catch (FileAccessDeniedException exception) {
            throw exception;
        } catch (FileNotFoundException exception) {
            log.warn(
                    "event=trainer_profile_file_not_found, " +
                            "requesterId={}, fileId={}",
                    requesterId,
                    fileId
            );

            return null;
        } catch (RuntimeException exception) {
            log.error(
                    "event=trainer_profile_file_url_resolve_failed, " +
                            "requesterId={}, fileId={}",
                    requesterId,
                    fileId,
                    exception
            );

            throw exception;
        }
    }
}
