package com.ssambbong.gymjjak.trainer.trainerprofile.application.service;

import com.ssambbong.gymjjak.file.application.result.FileUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.file.exception.FileAccessDeniedException;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.*;
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
                                new TrainerProfileNotFoundException(
                                        "userId",
                                        requesterId
                                )
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

    @Override
    public TrainerProfileDetailResult getTrainerProfileDetail(Long trainerProfileId) {
        log.info(
                "event=trainer_profile_detail_query_start," +
                        "trainerProfileId={}",
                trainerProfileId
        );

        TrainerProfile profile =
                trainerProfileRepository.findById(trainerProfileId)
                        .orElseThrow(() ->
                                new TrainerProfileNotFoundException(
                                        "trainerProfileId",
                                        trainerProfileId
                                )
                        );

        // 필수 자격증 리스트 반환
        List<TrainerCertificationSummaryResult> certifications =
                trainerCertificationRepository.findAllByTrainerProfileId(
                        profile.getTrainerProfileId()
                ).stream()
                        .map(this::toCertificationSummaryResult)
                        .toList();


        // 수상 결겨 리스트 반환
        List<TrainerAwardResult> awards =
                trainerAwardRepository.findAllByTrainerProfileId(
                        profile.getTrainerProfileId()
                ).stream()
                        .map(this::toAwardResult)
                        .toList();

        // 공개 이미지 url 반환
        String profileImageUrl =
                resolvePublicProfileImageUrl(
                        profile.getProfileFileId()
                );

        log.info(
                "event=trainer_profile_detail_query_succeeded, " +
                        "trainerProfileId={}, certificationCount={}, awardCount={}",
                trainerProfileId,
                certifications.size(),
                awards.size()
        );

        return new TrainerProfileDetailResult(
                profile.getTrainerProfileId(),
                profileImageUrl,
                profile.getTrainerName(),
                profile.getIntroduction(),
                profile.getAverageRating(),
                profile.getReviewCount(),
                profile.getStatus(),
                certifications,
                awards
        );
    }

    private String resolvePublicProfileImageUrl(Long profileFileId) {

        if (profileFileId == null) {
            return null;
        }

        try {
            FileUrlResult file =
                    fileUrlUseCase.getUrl(
                            profileFileId,
                            null,
                            false
                    );

            return file.url();
        } catch (FileNotFoundException exception) {
            log.warn(
                    "event=trainer_profile_public_image_not_found, " +
                            "profileFileId={}",
                    profileFileId
            );

            return null;
        } catch (RuntimeException exception) {
            log.error(
                    "event=trainer_profile_public_image_resolve_failed, " +
                            "profileFileId={}",
                    profileFileId,
                    exception
            );

            return null;
        }
    }

    private TrainerCertificationSummaryResult toCertificationSummaryResult(TrainerCertification certification) {
        return new TrainerCertificationSummaryResult(
                certification.getTrainerCertificationId(),
                certification.getName(),
                certification.getCertificationType()
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
