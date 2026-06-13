package com.ssambbong.gymjjak.pt.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.application.port.PtCourseEnrichQueryPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 임시 구현체
 * Organization/TrainerProfile 도메인 구현 후 교체 예정
 */
@Component
@RequiredArgsConstructor
public class PtCourseEnrichQueryAdapter implements PtCourseEnrichQueryPort {

    private final EntityManager em;

    @Override
    public OrganizationInfo findOrganizationById(Long organizationId) {
        Object[] result = (Object[]) em.createNativeQuery("""
                SELECT o.business_name, o.road_address,
                       o.latitude, o.longitude,
                       o.facility_phone, o.website_url, o.instagram_url
                FROM organizations o
                WHERE o.organization_id = :organizationId
                """)
                .setParameter("organizationId", organizationId)
                .getSingleResult();

        return new OrganizationInfo(
                (String) result[0],
                (String) result[1],
                result[2] != null ? ((Number) result[2]).doubleValue() : null,
                result[3] != null ? ((Number) result[3]).doubleValue() : null,
                (String) result[4],
                (String) result[5],
                (String) result[6]
        );
    }

//    @Override
//    public TrainerDisplayInfo findTrainerProfileById(Long trainerProfileId) {
//        Object[] result = (Object[]) em.createNativeQuery("""
//                SELECT tp.trainer_name, tp.qualifications, tp.award_histories, tp.introduction,
//                       tp.average_rating, tp.review_count, tp.profile_file_id
//                FROM trainer_profiles tp
//                WHERE tp.trainer_profile_id = :trainerProfileId
//                """)
//                .setParameter("trainerProfileId", trainerProfileId)
//                .getSingleResult();
//
//        return new TrainerDisplayInfo(
//                (String) result[0], // trainer_name
//                (String) result[1], // qualifications
//                (String) result[2], // award_histories
//                (String) result[3], // introduction
//                result[4] != null ? ((Number) result[4]).doubleValue() : null,
//                result[5] != null ? ((Number) result[5]).intValue() : 0,
//                result[6] != null ? ((Number) result[6]).longValue() : null
//        );
//    }

    @Override
    public TrainerDisplayInfo findTrainerProfileById(Long trainerProfileId) {
        Object[] result = (Object[]) em.createNativeQuery("""
            SELECT tp.trainer_name,
                   tp.introduction,
                   tp.average_rating,
                   tp.review_count,
                   tp.profile_file_id
            FROM trainer_profiles tp
            WHERE tp.trainer_profile_id = :trainerProfileId
              AND tp.deleted_at IS NULL
            """)
                .setParameter("trainerProfileId", trainerProfileId)
                .getSingleResult();

        /* Comment
        *   새로 아래 만든, private 메서드 참조
        * */
        List<String> qualifications = findTrainerCertificationNames(trainerProfileId);
        List<String> awardHistories = findTrainerAwardNames(trainerProfileId);

        return new TrainerDisplayInfo(
                (String) result[0],
                qualifications,
                awardHistories,
                (String) result[1],
                result[2] != null ? ((Number) result[2]).doubleValue() : null,
                result[3] != null ? ((Number) result[3]).intValue() : 0,
                result[4] != null ? ((Number) result[4]).longValue() : null
        );
    }

    /* Comment
    *   이제 트레이너 프로필에 자격증, 수상경력이 직접 들어있는게 아니라
    *   FK로 각 테이블을 참조하는 방싱이여서 native Query를 따로 작성했어
    *   슬랙 참고해줘
    * */
    private List<String> findTrainerCertificationNames(Long trainerProfileId) {
        List<?> rows = em.createNativeQuery("""
            SELECT tc.name
            FROM trainer_certifications tc
            WHERE tc.trainer_profile_id = :trainerProfileId
              AND tc.deleted_at IS NULL
            ORDER BY tc.trainer_certification_id
            """)
                .setParameter("trainerProfileId", trainerProfileId)
                .getResultList();

        return rows.stream()
                .map(String.class::cast)
                .toList();
    }

    private List<String> findTrainerAwardNames(Long trainerProfileId) {
        List<?> rows = em.createNativeQuery("""
            SELECT ta.name
            FROM trainer_awards ta
            WHERE ta.trainer_profile_id = :trainerProfileId
              AND ta.deleted_at IS NULL
            ORDER BY ta.trainer_award_id
            """)
                .setParameter("trainerProfileId", trainerProfileId)
                .getResultList();

        return rows.stream()
                .map(String.class::cast)
                .toList();
    }
}
