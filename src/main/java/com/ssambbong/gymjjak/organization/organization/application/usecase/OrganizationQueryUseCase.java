package com.ssambbong.gymjjak.organization.organization.application.usecase;

import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;

public interface OrganizationQueryUseCase {

    // 본인 조직 조회 (ORGANIZATION 계정용)
    Organization findMyOrganization(Long organizationAccountId);
}
