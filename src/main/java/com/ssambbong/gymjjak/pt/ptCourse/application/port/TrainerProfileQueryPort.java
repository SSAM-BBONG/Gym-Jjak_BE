package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.dto.TrainerSummaryInfo;

import java.util.List;
import java.util.Map;

// 트레이너 프로필 조회 Port
public interface TrainerProfileQueryPort {

    // 활성화중인 트레이너 프로필 id 조회
    Long findActiveTrainerProfileIdByUserId(Long userId);

    // 활동 중인 트레이너 수
    long countActive();

    // 트레이너 프로필 -> 전체 평균 만족도
    Double averageRating();

    // 트레이너 name 조회
    String findTrainerNameById(Long trainerProfileId);

    // 목록 조회용 요약 정보 배치 조회 (N+1 방지)
    Map<Long, TrainerSummaryInfo> findSummaryAllByIds(List<Long> ids);

    // 상세 조회용 전체 정보 조회
    TrainerDisplayInfo findById(Long trainerProfileId);

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
