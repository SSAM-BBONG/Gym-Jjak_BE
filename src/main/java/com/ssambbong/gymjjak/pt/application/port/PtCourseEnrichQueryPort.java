package com.ssambbong.gymjjak.pt.application.port;

/**
 * PT 강습 조회 시 필요한 조직/트레이너 부가 정보 조회 포트
 * - 임시 구현체: PtCourseEnrichQueryAdapter (EntityManager 직접 쿼리)
 * - Organization/TrainerProfile 도메인 구현 후 교체 예정
 */
public interface PtCourseEnrichQueryPort {

    OrganizationInfo findOrganizationById(Long organizationId);

    TrainerDisplayInfo findTrainerProfileById(Long trainerProfileId);

    record OrganizationInfo(
            String name,
            String address,
            Double latitude,
            Double longitude,
            String phone,
            String websiteUrl,
            String instagramUrl
    ) {}

    record TrainerDisplayInfo(
            String name,
            String spec,
            String introduction,
            Double averageRating,
            int reviewCount
    ) {}
}
