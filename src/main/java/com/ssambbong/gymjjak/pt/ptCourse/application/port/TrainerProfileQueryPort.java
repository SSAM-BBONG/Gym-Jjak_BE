package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.util.List;
import java.util.Map;

// 트레이너 프로필 조회 Port
public interface TrainerProfileQueryPort {

    // TODO: 이거 지우기! Adapter랑 JpaRepo까지 지워주세요!
    TrainerInfo findByUserId(Long userId);

    // 활성화중인 트레이너 프로필 id 조회
    Long findActiveTrainerProfileIdByUserId(Long userId);

    // 활동 중인 트레이너 수
    long countActive();

    // 트레이너 프로필 -> 전체 평균 만족도
    Double averageRating();

    // 트레이너 name 조회
    String findTrainerNameById(Long trainerProfileId);

    // TODO: dto 클래스를 반환타입으로 바꾸기
    Map<Long, TrainerSummaryInfo> findSummaryAllByIds(List<Long> ids);

    // 상세 조회용 전체 정보 조회
    TrainerDisplayInfo findById(Long trainerProfileId);

    record TrainerInfo(
            Long trainerProfileId,
            Long organizationId
    ) {}

    // TODO: pt 도메인에서 dto로 만들
    //   pt/ptCourse/application/port/dto/TrainerSummaryInfo.java로 만들기,
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
