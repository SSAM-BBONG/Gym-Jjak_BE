package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.util.List;

// 트레이너 프로필 조회 Port
public interface TrainerProfileQueryPort {

    TrainerInfo findByUserId(Long userId);

    // 목록 조회용 경량 조회 (자격증/수상 제외)
    TrainerSummaryInfo findSummaryById(Long trainerProfileId);

    // 상세 조회용 전체 정보 조회
    TrainerDisplayInfo findById(Long trainerProfileId);

    record TrainerInfo(
            Long trainerProfileId,
            Long organizationId
    ) {}

    // 목록 조회용 경량 DTO (trainerName, reviewCount만 포함)
    record TrainerSummaryInfo(
            String trainerName,
            int reviewCount
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
