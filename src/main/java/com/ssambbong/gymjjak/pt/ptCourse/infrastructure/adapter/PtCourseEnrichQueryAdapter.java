package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.PtCourseEnrichQueryPort;
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
                WHERE o.organization_id = ?1
                """)
                .setParameter(1, organizationId)
                .getSingleResult();

        return new OrganizationInfo(
                organizationId,
                (String) result[0],
                (String) result[1],
                result[2] != null ? ((Number) result[2]).doubleValue() : null,
                result[3] != null ? ((Number) result[3]).doubleValue() : null,
                (String) result[4],
                (String) result[5],
                (String) result[6]
        );
    }

    @Override
    public TrainerDisplayInfo findTrainerProfileById(Long trainerProfileId) {
        Object[] result = (Object[]) em.createNativeQuery("""
                SELECT tp.trainer_name,
                       tp.introduction,
                       tp.average_rating,
                       tp.review_count,
                       tp.profile_file_id
                FROM trainer_profiles tp
                WHERE tp.trainer_profile_id = ?1
                  AND tp.deleted_at IS NULL
                """)
                .setParameter(1, trainerProfileId)
                .getSingleResult();

        List<String> certifications = findTrainerCertificationNames(trainerProfileId);
        List<String> awards = findTrainerAwardNames(trainerProfileId);

        return new TrainerDisplayInfo(
                (String) result[0],                                              // trainerName
                (String) result[1],                                              // introduction
                result[2] != null ? ((Number) result[2]).doubleValue() : null,  // averageRating
                result[3] != null ? ((Number) result[3]).intValue() : 0,        // reviewCount
                result[4] != null ? ((Number) result[4]).longValue() : null,    // profileFileId
                certifications,
                awards
        );
    }

    private List<String> findTrainerCertificationNames(Long trainerProfileId) {
        List<?> rows = em.createNativeQuery("""
                SELECT tc.name
                FROM trainer_certifications tc
                WHERE tc.trainer_profile_id = ?1
                  AND tc.deleted_at IS NULL
                ORDER BY tc.trainer_certification_id
                """)
                .setParameter(1, trainerProfileId)
                .getResultList();

        return rows.stream()
                .map(String.class::cast)
                .toList();
    }

    private List<String> findTrainerAwardNames(Long trainerProfileId) {
        List<?> rows = em.createNativeQuery("""
                SELECT ta.name
                FROM trainer_awards ta
                WHERE ta.trainer_profile_id = ?1
                  AND ta.deleted_at IS NULL
                ORDER BY ta.trainer_award_id
                """)
                .setParameter(1, trainerProfileId)
                .getResultList();

        return rows.stream()
                .map(String.class::cast)
                .toList();
    }
}
