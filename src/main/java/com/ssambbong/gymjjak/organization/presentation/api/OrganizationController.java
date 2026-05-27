package com.ssambbong.gymjjak.organization.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.organization.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.application.usecase.OrganizationApplicationUsecase;
import com.ssambbong.gymjjak.organization.presentation.api.request.OrganizationApplicationCreateRequest;
import com.ssambbong.gymjjak.organization.presentation.api.response.OrganizationApplicationCreateResponse;
import com.ssambbong.gymjjak.organization.presentation.api.response.OrganizationApplicationResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Organization Application", description = "조직 신청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organization-applications")
public class OrganizationController {

    private final OrganizationApplicationUsecase organizationApplicationUsecase;

    @Operation(summary = "조직 신청", description = "사용자가 조직(헬스장) 등록을 신청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "조직 신청 성공",
                    content = @Content(schema = @Schema(implementation = OrganizationApplicationCreateResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 사업자등록번호",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping
    public GlobalApiResponse<OrganizationApplicationCreateResponse> createOrganizationApplication(
            @RequestBody @Valid OrganizationApplicationCreateRequest request) {

        Long applicantUserId = request.applicantUserId();

        Long organizationApplicationId = organizationApplicationUsecase.createOrganizationApplication(new OrganizationApplicationCreateCommand(
                applicantUserId,
                request.requestedLoginId(),
                request.businessLicenseFileId(),
                request.businessRegistrationNumber(),
                request.businessName(),
                request.representativeName(),
                request.representativePhone(),
                request.openingDate(),
                request.roadAddress(),
                request.jibunAddress(),
                request.detailAddress(),
                request.latitude(),
                request.longitude(),
                request.websiteUrl(),
                request.instagramUrl(),
                request.blogUrl(),
                request.facilityPhone()
        ));

        return GlobalApiResponse.created(
                OrganizationApplicationResponseCode.ORGANIZATION_APPLICATION_CREATED,
                new OrganizationApplicationCreateResponse(organizationApplicationId)
        );
    }
}
