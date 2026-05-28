package com.ssambbong.gymjjak.organization.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.organization.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.application.usecase.OrganizationApplicationCommandUsecase;
import com.ssambbong.gymjjak.organization.application.usecase.OrganizationApplicationQueryUsecase;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.presentation.api.request.OrganizationApplicationCreateRequest;
import com.ssambbong.gymjjak.organization.presentation.api.response.FindMyOrganizationApplicationResponse;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Organization Application", description = "조직 신청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organization-applications")
public class OrganizationController {

    private final OrganizationApplicationCommandUsecase organizationApplicationCommandUsecase;
    private final OrganizationApplicationQueryUsecase organizationApplicationQueryUsecase;

    @Operation(summary = "조직 신청", description = "사용자가 조직(헬스장) 등록을 신청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "조직 신청 성공",
                    content = @Content(schema = @Schema(implementation = OrganizationApplicationCreateResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 사업자등록번호",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping
    public GlobalApiResponse<OrganizationApplicationCreateResponse> createOrganizationApplication(
            @RequestPart("file") MultipartFile businessLicenseFile,
            @RequestPart("request") @Valid OrganizationApplicationCreateRequest request) {

        Long applicantUserId = request.applicantUserId();

        Long organizationApplicationId = organizationApplicationCommandUsecase.createOrganizationApplication(businessLicenseFile, new OrganizationApplicationCreateCommand(
                applicantUserId,
                request.requestedLoginId(),
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

    @Operation(summary = "내 조직 신청 목록 조회", description = "로그인한 사용자의 조직 신청 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindMyOrganizationApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/me")
    public GlobalApiResponse<List<FindMyOrganizationApplicationResponse>> findMyOrganizationApplications(
            @RequestParam Long userId
    ) {
        List<OrganizationApplication> myOrganizationApplication = organizationApplicationQueryUsecase.findMyOrganizationApplications(userId);

        List<FindMyOrganizationApplicationResponse> response = myOrganizationApplication.stream()
                .map(domain -> new FindMyOrganizationApplicationResponse(
                        domain.getOrganizationApplicationId(),
                        domain.getBusinessName(),
                        domain.getRequestedLoginId(),
                        domain.getStatus(),
                        domain.getBusinessRegistrationNumber(),
                        domain.getRepresentativeName(),
                        domain.getCreatedAt()
                ))
                .toList();

        return GlobalApiResponse.ok(
                OrganizationApplicationResponseCode.ORGANIZATION_APPLICATION_FOUND,
                response);
    }
}
