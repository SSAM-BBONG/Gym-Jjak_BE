package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.util.List;

// 트레이너 프로필 조회 Port
public interface TrainerProfileQueryPort {

    TrainerInfo findByUserId(Long userId);

    TrainerDisplayInfo findById(Long trainerProfileId);

    record TrainerInfo(
            Long trainerProfileId,
            Long organizationId
    ) {}

    record TrainerDisplayInfo(
            String trainerName,
            String introduction,
            Double averageRating,
            int reviewCount,
            Long profileFileId,
            List<String> certifications,
            List<String> awards
    ) {}
}
