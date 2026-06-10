package com.ssambbong.gymjjak.organization.organization.application.usecase;

import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.FindOrganizationsResponse;

import java.util.List;

public interface OrganizationQueryUseCase {

    // 본인 조직 조회 (ORGANIZATION 계정용)
    Organization findMyOrganization(Long organizationAccountId);

    // 전체 조직 목록 조회 (ADMIN용)
    List<FindOrganizationsResponse> findOrganizations();
}
