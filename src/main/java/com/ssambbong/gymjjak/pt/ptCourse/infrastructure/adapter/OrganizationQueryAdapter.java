package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.OrganizationQueryPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// TODO: Organization 도메인 Query Port 구현 후 EntityManager 직접 조회 제거

@Component
@RequiredArgsConstructor
public class OrganizationQueryAdapter implements OrganizationQueryPort {

    private final EntityManager em;

    @Override
    public OrganizationInfo findById(Long organizationId) {
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

}
