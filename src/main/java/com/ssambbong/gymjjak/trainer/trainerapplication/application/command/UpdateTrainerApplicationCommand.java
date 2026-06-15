package com.ssambbong.gymjjak.trainer.trainerapplication.application.command;

import java.util.List;

public record UpdateTrainerApplicationCommand(
        // 수정할 트레이너 신청 ID
        Long trainerApplicationId,

        // 수정 요청한 사용자 ID
        Long requesterId,

        // 변경할 프로필 이미지 fileId
        Long profileImageFileId,

        // 변경할 자격증 목록
        List<String> qualifications,

        // 변경할 수상/대회경력 목록
        List<String> awardHistories,

        // 변경할 자기소개
        String introduction
) {
}
