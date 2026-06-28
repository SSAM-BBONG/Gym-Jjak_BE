package com.ssambbong.gymjjak.organization.organization.infrastructure.adapter;

import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import com.ssambbong.gymjjak.organization.organization.exception.OrganizationNotFoundException;
import com.ssambbong.gymjjak.organization.organization.infrastructure.persistence.SpringDataOrganizationRepository;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.OrganizationQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrganizationQueryPortAdapter implements OrganizationQueryPort {

    private final OrganizationRepository organizationRepository;
    private final SpringDataOrganizationRepository springDataOrganizationRepository;

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
    public Map<Long, OrganizationInfo> findAllByIds(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        return springDataOrganizationRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        e -> e.getOrganizationId(),
                        e -> new OrganizationInfo(
                                e.getOrganizationId(),
                                e.getBusinessName(),
                                e.getRoadAddress(),
                                e.getLatitude() != null ? e.getLatitude().doubleValue() : null,
                                e.getLongitude() != null ? e.getLongitude().doubleValue() : null,
                                e.getFacilityPhone(),
                                e.getWebsiteUrl(),
                                e.getInstagramUrl()
                        )
                ));
    }

    @Override
    public long countActive() {
        return organizationRepository.countByStatus(OrganizationStatus.ACTIVE);
    }
}
