package com.ssambbong.gymjjak.pt.application.port;

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
            /* Comment
            *   현지야, 로직 짜려면 이쪽이 변경되어있어야되서, 일단 내가 수정했어 슬랙 적어
            *   놓을테니 보고 클러드한테 물어보고 공부해봐 헷갈리면 나한테 물어봐도 돼
            * */
            List<String> qualifications,
            List<String> awardHistories,
//            String qualifications,
//            String awardHistories,
            String introduction,
            Double averageRating,
            int reviewCount,
            Long profileFileId   // 프로필 이미지 파일 ID (null 가능)
    ) {}
}
