package com.ssambbong.gymjjak.pt.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.application.port.PtCourseEnrichQueryPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
                SELECT tp.display_name, tp.spec, tp.introduction,
                       tp.average_rating, tp.review_count, tp.profile_file_id
                FROM trainer_profiles tp
                WHERE tp.trainer_profile_id = ?1
                """)
                .setParameter(1, trainerProfileId)
                .getSingleResult();

        return new TrainerDisplayInfo(
                (String) result[0],
                (String) result[1],
                (String) result[2],
                result[3] != null ? ((Number) result[3]).doubleValue() : null,
                result[4] != null ? ((Number) result[4]).intValue() : 0,
                result[5] != null ? ((Number) result[5]).longValue() : null
        );
    }
}
