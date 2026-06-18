package com.ssambbong.gymjjak.trainer.trainerapplication.application.result;

public record TrainerApprovalUserInfo(
        // 트레이너로 승격된 userId
        Long userId,
        // trainer_profiles.trainer_name에 저장할 이름
        String name,
        // 이메일
        String username,
        // 닉네임
        String nickname
) {
}
