package com.ssambbong.gymjjak.organization.organization.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.security.principal.AuthUser;
import com.ssambbong.gymjjak.organization.organization.application.usecase.OrganizationCommandUseCase;
import com.ssambbong.gymjjak.organization.organization.application.usecase.OrganizationQueryUseCase;
import com.ssambbong.gymjjak.organization.organization.domain.model.Organization;
import com.ssambbong.gymjjak.organization.organization.presentation.api.request.OrganizationUpdateRequest;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.OrganizationResponse;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.OrganizationResponseCode;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Organization", description = "조직 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationQueryUseCase organizationQueryUseCase;
    private final OrganizationCommandUseCase organizationCommandUseCase;
    private final FileUseCase fileUseCase;

    @PreAuthorize("hasAuthority('ORGANIZATION')")
    @Operation(summary = "내 조직 정보 조회", description = "조직 계정이 본인 조직 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = OrganizationResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
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

    @PreAuthorize("hasAuthority('ORGANIZATION')")
    @Operation(summary = "내 조직 정보 수정", description = "조직 계정이 본인 조직의 추가 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (전화번호 형식 오류 등)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @PatchMapping("/me")
    public ResponseEntity<GlobalApiResponse<Void>> updateMyOrganization(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid OrganizationUpdateRequest request
    ) {
        organizationCommandUseCase.updateOrganization(
                authUser.userId(),
                request.facilityPhone(),
                request.instagramUrl(),
                request.blogUrl(),
                request.websiteUrl()
        );

        return ResponseEntity.ok(
                GlobalApiResponse.ok(OrganizationResponseCode.ORGANIZATION_UPDATED, null)
        );
    }

}
