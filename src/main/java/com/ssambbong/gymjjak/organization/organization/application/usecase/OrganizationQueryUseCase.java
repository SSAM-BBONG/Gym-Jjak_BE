package com.ssambbong.gymjjak.organization.organization.application.usecase;

import com.ssambbong.gymjjak.organization.organization.application.query.MyOrganizationResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationAdminDetailResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationDetailResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListQuery;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationSearchListResult;

public interface OrganizationQueryUseCase {

    // 본인 조직 조회 (ORGANIZATION 계정용)
    MyOrganizationResult findMyOrganization(Long organizationAccountId);

    // 전체 조직 목록 조회 (ADMIN용)
    OrganizationListResult findOrganizations(OrganizationListQuery query);

    // 조직 상세 조회 - ADMIN용 (trainers + requestedLoginId 포함)
    OrganizationAdminDetailResult findOrganizationAdminDetail(Long organizationId);

    // 조직 상세 조회 - 사용자용
    OrganizationDetailResult findOrganizationDetail(Long organizationId);

    // 조직 검색 (사용자/트레이너용)
    OrganizationSearchListResult searchOrganizations(String keyword, int page, int size);
}
