package com.ssambbong.gymjjak.trainer.trainerapplication.application.command;

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
}
