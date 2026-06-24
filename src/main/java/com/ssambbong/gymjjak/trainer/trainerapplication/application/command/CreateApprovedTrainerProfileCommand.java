package com.ssambbong.gymjjak.trainer.trainerapplication.application.command;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.InvalidTrainerApplicationException;

import java.util.List;

public record CreateApprovedTrainerProfileCommand(
        // 승인된 userId
        Long userId,

        // 트레이너 신청 id
        Long trainerApplicationId,

        // 신청 당시 프로필 이미지 fileId.
        Long profileFileId,

        // trainer_profiles.trainer_name에 저장할 이름.
        String trainerName,

        // trainer_profiles.introduction에 저장할 자기소개
        String introduction,

        // 자격증 목록.
        // 승인 후 trainer_certifications에 ADDITIONAL으로 저장
        List<String> qualifications,

        // 필수 자격증 fileId
        // 승인 후 trainer_certifications에 REQUIRED으로 저장
        Long certificateFileId,

        // 수상/대회경력 목록.
        // 승인 후 trainer_awards에 저장
        List<String> awardHistories
) {
    public CreateApprovedTrainerProfileCommand {
        if (userId == null || userId <= 0) {
            throw new InvalidTrainerApplicationException("userId는 1 이상이어야 합니다.");
        }
        if (trainerApplicationId == null || trainerApplicationId <= 0) {
            throw new InvalidTrainerApplicationException("trainerApplicationId는 1 이상이어야 합니다.");
        }
        if (certificateFileId == null || certificateFileId <= 0) {
            throw new InvalidTrainerApplicationException("certificateFileId는 1 이상이어야 합니다.");
        }
        if (trainerName == null || trainerName.isBlank()) {
            throw new InvalidTrainerApplicationException("trainerName은 비어 있을 수 없습니다.");
        }

        qualifications = qualifications == null ? List.of() : List.copyOf(qualifications);
        awardHistories = awardHistories == null ? List.of() : List.copyOf(awardHistories);

    }
}
