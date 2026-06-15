package com.ssambbong.gymjjak.pt.ptCourse.application.port;

import java.util.List;

/**
 * PT 강습 조회 시 필요한 조직/트레이너 부가 정보 조회 포트
 * - 임시 구현체: PtCourseEnrichQueryAdapter (EntityManager 직접 쿼리)
 * - Organization/TrainerProfile 도메인 구현 후 교체 예정
 */
public interface PtCourseEnrichQueryPort {

    OrganizationInfo findOrganizationById(Long organizationId);

    TrainerDisplayInfo findTrainerProfileById(Long trainerProfileId);

    record OrganizationInfo(
            Long organizationId,
            String businessName,
            String roadAddress,
            Double latitude,
            Double longitude,
            String phone,
            String websiteUrl,
            String instagramUrl
    ) {}

    record TrainerDisplayInfo(
            String trainerName,       // DB: trainer_name
            String introduction,
            Double averageRating,
            int reviewCount,
            Long profileFileId,
            List<String> certifications,  // trainer_certifications 테이블
            List<String> awards           // trainer_awards 테이블
    ) {}
}
