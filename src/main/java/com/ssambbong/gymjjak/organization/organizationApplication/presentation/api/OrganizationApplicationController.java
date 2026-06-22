package com.ssambbong.gymjjak.organization.organizationApplication.presentation.api;

import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.organization.organizationApplication.application.command.OrganizationApplicationCreateCommand;
import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListQuery;
import com.ssambbong.gymjjak.organization.organizationApplication.application.query.ApplicationListResult;
import com.ssambbong.gymjjak.organization.organizationApplication.application.usecase.OrganizationApplicationCommandUsecase;
import com.ssambbong.gymjjak.organization.organizationApplication.application.usecase.OrganizationApplicationQueryUsecase;
import com.ssambbong.gymjjak.organization.organizationApplication.domain.model.OrganizationApplication;
import com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.mapper.OrganizationApplicationMapper;
import com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.request.OrganizationApplicationCreateRequest;
import com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.request.RejectOrganizationApplicationRequest;
import com.ssambbong.gymjjak.organization.organizationApplication.presentation.api.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Organization Application", description = "조직 신청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organization-applications")
@Validated
public class OrganizationApplicationController {

    private final OrganizationApplicationCommandUsecase organizationApplicationCommandUsecase;
    private final OrganizationApplicationQueryUsecase organizationApplicationQueryUsecase;
    private final OrganizationApplicationMapper organizationApplicationMapper;
    private final FileUrlUseCase fileUrlUseCase;

    @Operation(summary = "조직 신청", description = "사용자가 조직(헬스장) 등록을 신청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "조직 신청 성공",
                    content = @Content(schema = @Schema(implementation = OrganizationApplicationCreateResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 사업자등록번호",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping
    public ResponseEntity<GlobalApiResponse<OrganizationApplicationCreateResponse>> createOrganizationApplication(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid OrganizationApplicationCreateRequest request) {

        Long organizationApplicationId = organizationApplicationCommandUsecase.createOrganizationApplication(
                new OrganizationApplicationCreateCommand(
                        authUser.userId(),
                        request.fileId(),
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

        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(
                        OrganizationApplicationResponseCode.ORGANIZATION_APPLICATION_CREATED,
                        new OrganizationApplicationCreateResponse(organizationApplicationId)
                ));
    }

    @Operation(summary = "내 조직 신청 목록 조회", description = "로그인한 사용자의 조직 신청 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindMyOrganizationApplicationResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/me")
    public ResponseEntity<GlobalApiResponse<List<FindMyOrganizationApplicationResponse>>> findMyOrganizationApplications(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        Long applicantUserId = authUser.userId();

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

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        OrganizationApplicationResponseCode.ORGANIZATION_APPLICATION_FOUND,
                        response));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "관리자 조직 신청 전체 목록 조회", description = "관리자가 모든 조직 신청 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindAllOrganizationApplicationsListResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public ResponseEntity<GlobalApiResponse<FindAllOrganizationApplicationsListResponse>> findAllOrganizationApplications(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        ApplicationListQuery query = new ApplicationListQuery(page, size);
        ApplicationListResult result = organizationApplicationQueryUsecase.findPendingOrganizationApplications(query);
        FindAllOrganizationApplicationsListResponse response = organizationApplicationMapper.toListResponse(result);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        OrganizationApplicationResponseCode.ORGANIZATION_APPLICATION_ALL_FOUND,
                        response));
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
    public ResponseEntity<GlobalApiResponse<FindOrganizationApplicationDetailsResponse>> findOrganizationApplicationDetails(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        Long requestUserId = authUser.userId();
        boolean isAdmin = authUser.role().equals("ADMIN");

        OrganizationApplication organizationApplicationDetails =
                organizationApplicationQueryUsecase.findOrganizationApplicationDetails(applicationId, requestUserId, isAdmin);

        String businessLicenseFileUrl = fileUrlUseCase.getUrl(
                organizationApplicationDetails.getBusinessLicenseFileId(), requestUserId, isAdmin).url();

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
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
                                businessLicenseFileUrl
                        )
                )
        );
    }

    @Operation(summary = "조직 신청 승인", description = "관리자가 조직 신청을 승인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 성공",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "신청 내역 없음",
                    content = @Content(schema = @Schema()))
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{applicationId}/approve")
    public ResponseEntity<GlobalApiResponse<Void>> approveOrganizationApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        organizationApplicationCommandUsecase.approveOrganizationApplication(applicationId, authUser.userId());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(OrganizationApplicationResponseCode.ORGANIZATION_APPLICATION_APPROVED, null));
    }

    @Operation(summary = "조직 신청 반려", description = "관리자가 조직 신청을 반려합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "반려 성공",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "신청 내역 없음",
                    content = @Content(schema = @Schema()))
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{applicationId}/reject")
    public ResponseEntity<GlobalApiResponse<Void>> rejectOrganizationApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid RejectOrganizationApplicationRequest request
    ) {
        organizationApplicationCommandUsecase.rejectOrganizationApplication(
                applicationId, authUser.userId(), request.rejectReason());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(OrganizationApplicationResponseCode.ORGANIZATION_APPLICATION_REJECTED, null));
    }

    @Operation(summary = "조직 신청 취소", description = "사용자가 본인의 조직 신청을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "신청 내역을 찾을 수 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "취소할 수 없는 상태의 신청",
                    content = @Content(schema = @Schema()))
    })
    @PatchMapping("/{applicationId}/cancel")
    public ResponseEntity<GlobalApiResponse<Void>> cancelOrganizationApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        organizationApplicationCommandUsecase.cancelOrganizationApplication(applicationId, authUser.userId());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(OrganizationApplicationResponseCode.ORGANIZATION_APPLICATION_CANCEL, null));
    }
}
