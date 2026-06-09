package com.ssambbong.gymjjak.organization.organization.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.security.principal.AuthUser;
import com.ssambbong.gymjjak.organization.organization.application.usecase.OrganizationQueryUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.OrganizationResponse;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.OrganizationResponseCode;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Organization", description = "조직 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationQueryUseCase organizationQueryUseCase;
    private final FileUseCase fileUseCase;

    @Operation(summary = "내 조직 정보 조회", description = "조직 계정이 본인 조직 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<GlobalApiResponse<OrganizationResponse>> getMyOrganization(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        Organization organization = organizationQueryUseCase.findMyOrganization(authUser.userId());

        String businessLicenseUrl = fileUseCase.getPresignedUrl(organization.getBusinessLicenseFileId());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        OrganizationResponseCode.ORGANIZATION_FOUND,
                        OrganizationResponse.of(organization, businessLicenseUrl)
                )
        );
    }

}
