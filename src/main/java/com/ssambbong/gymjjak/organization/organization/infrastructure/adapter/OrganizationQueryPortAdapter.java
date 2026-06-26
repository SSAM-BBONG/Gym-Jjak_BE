package com.ssambbong.gymjjak.organization.organization.infrastructure.adapter;

import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.OrganizationQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationQueryPortAdapter implements OrganizationQueryPort {

    private final OrganizationRepository organizationRepository;

    @Override
    public OrganizationInfo findById(Long organizationId) {
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);
        return new OrganizationInfo(
                org.getOrganizationId(),
                org.getBusinessName(),
                org.getRoadAddress(),
                org.getLatitude() != null ? org.getLatitude().doubleValue() : null,
                org.getLongitude() != null ? org.getLongitude().doubleValue() : null,
                org.getFacilityPhone(),
                org.getWebsiteUrl(),
                org.getInstagramUrl()
        );
    }

    @Override
    public long countActive() {
        return organizationRepository.countByStatus(OrganizationStatus.ACTIVE);
    }
}
