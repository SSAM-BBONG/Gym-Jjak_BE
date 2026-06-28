package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.util.List;
import java.util.Map;

// 트레이너 프로필 조회 Port
public interface TrainerProfileQueryPort {

    TrainerInfo findByUserId(Long userId);

    // 활동 중인 트레이너 수
    long countActive();

    // 트레이너 프로필 -> 전체 평균 만족도
    Double averageRating();

    // 목록 조회용 경량 조회 (자격증/수상 제외)
    TrainerSummaryInfo findSummaryById(Long trainerProfileId);

    Map<Long, TrainerSummaryInfo> findSummaryAllByIds(List<Long> ids);

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
