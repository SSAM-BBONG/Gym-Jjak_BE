package com.ssambbong.gymjjak.organization.presentation.api;

import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.organization.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.application.usecase.OrganizationApplicationCommandUsecase;
import com.ssambbong.gymjjak.organization.application.usecase.OrganizationApplicationQueryUsecase;
import com.ssambbong.gymjjak.organization.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.presentation.api.request.OrganizationApplicationCreateRequest;
import com.ssambbong.gymjjak.organization.presentation.api.response.FindMyOrganizationApplicationResponse;
import com.ssambbong.gymjjak.organization.presentation.api.response.FindOrganizationApplicationDetailsResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final FileUseCase fileUseCase;

    @Operation(summary = "조직 신청", description = "사용자가 조직(헬스장) 등록을 신청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "조직 신청 성공",
                    content = @Content(schema = @Schema(implementation = OrganizationApplicationCreateResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 사업자등록번호",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping
    public GlobalApiResponse<OrganizationApplicationCreateResponse> createOrganizationApplication(
            @AuthenticationPrincipal String userId,
            @RequestPart("file") MultipartFile businessLicenseFile,
            @RequestPart("request") @Valid OrganizationApplicationCreateRequest request) {

        Long applicantUserId = Long.valueOf(userId);

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
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/me")
    public GlobalApiResponse<List<FindMyOrganizationApplicationResponse>> findMyOrganizationApplications(
            @AuthenticationPrincipal String userId
    ) {
        Long applicantUserId = Long.valueOf(userId);

        List<OrganizationApplication> myOrganizationApplication = organizationApplicationQueryUsecase.findMyOrganizationApplications(applicantUserId);

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

    @Operation(summary = "조직 신청 상세 조회", description = "조직 신청 건의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindOrganizationApplicationDetailsResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "신청 내역을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })

    @GetMapping("/{applicationId}")
    public GlobalApiResponse<FindOrganizationApplicationDetailsResponse> findOrganizationApplicationDetails(
            @PathVariable Long applicationId,
            Authentication authentication
    ) {
        Long requestUserId = Long.parseLong((String) authentication.getPrincipal());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        OrganizationApplication organizationApplicationDetails =
                organizationApplicationQueryUsecase.findOrganizationApplicationDetails(applicationId, requestUserId, isAdmin);

        String presignedUrl = fileUseCase.getPresignedUrl(organizationApplicationDetails.getBusinessLicenseFileId());

        return GlobalApiResponse.ok(
                OrganizationApplicationResponseCode.ORGANIZATION_APPLICATION_DETAILS_FOUND,
                new FindOrganizationApplicationDetailsResponse(
                        organizationApplicationDetails.getOrganizationApplicationId(),
                        organizationApplicationDetails.getRequestedLoginId(),
                        organizationApplicationDetails.getBusinessRegistrationNumber(),
                        organizationApplicationDetails.getBusinessName(),
                        organizationApplicationDetails.getRepresentativeName(),
                        organizationApplicationDetails.getRepresentativePhone(),
                        organizationApplicationDetails.getOpeningDate(),
                        organizationApplicationDetails.getRoadAddress(),
                        organizationApplicationDetails.getJibunAddress(),
                        organizationApplicationDetails.getDetailAddress(),
                        organizationApplicationDetails.getLatitude(),
                        organizationApplicationDetails.getLongitude(),
                        organizationApplicationDetails.getWebsiteUrl(),
                        organizationApplicationDetails.getInstagramUrl(),
                        organizationApplicationDetails.getBlogUrl(),
                        organizationApplicationDetails.getFacilityPhone(),
                        presignedUrl
                )
        );
    }

}
