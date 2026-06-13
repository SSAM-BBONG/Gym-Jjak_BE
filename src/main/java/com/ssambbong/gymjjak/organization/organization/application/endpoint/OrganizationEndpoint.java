package com.ssambbong.gymjjak.organization.organization.application.endpoint;

import com.ssambbong.gymjjak.organization.organization.domain.model.OrganizationStatus;
import com.ssambbong.gymjjak.organization.organization.domain.repository.OrganizationRepository;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "organization")
public class OrganizationEndpoint {

    private final OrganizationRepository organizationRepository;

    public OrganizationEndpoint(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @ReadOperation
    public OrganizationSummary summary() {
        return new OrganizationSummary(
                organizationRepository.countByStatus(OrganizationStatus.ACTIVE)
        );
    }

    public record OrganizationSummary(
            long activeOrganizationCount
            // 조직별 평균 트레이너 수 → 트레이너 도메인 구현 후 추가 예정
    ) {}
}
