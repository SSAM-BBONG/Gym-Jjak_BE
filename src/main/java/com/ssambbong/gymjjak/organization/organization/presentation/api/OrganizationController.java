package com.ssambbong.gymjjak.organization.organization.presentation.api;

import com.ssambbong.gymjjak.file.application.result.FileUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.organization.organization.application.command.OrganizationUpdateCommand;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationAdminDetailResult;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListQuery;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationListResult;
import com.ssambbong.gymjjak.organization.organization.application.query.MyOrganizationResult;
import com.ssambbong.gymjjak.organization.organization.application.usecase.OrganizationCommandUseCase;
import com.ssambbong.gymjjak.organization.organization.application.usecase.OrganizationQueryUseCase;
import com.ssambbong.gymjjak.organization.organization.presentation.api.mapper.OrganizationMapper;
import com.ssambbong.gymjjak.organization.organization.presentation.api.request.OrganizationUpdateRequest;
import com.ssambbong.gymjjak.organization.organization.presentation.api.response.*;
import com.ssambbong.gymjjak.organization.organization.application.query.OrganizationSearchListResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Organization", description = "조직 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations")
@Validated
public class OrganizationController {

    private final OrganizationQueryUseCase organizationQueryUseCase;
    private final OrganizationCommandUseCase organizationCommandUseCase;
    private final OrganizationMapper organizationMapper;
    private final FileUrlUseCase fileUrlUseCase;

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "조직 목록 조회 (관리자)", description = "관리자가 전체 조직 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindOrganizationsResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public ResponseEntity<GlobalApiResponse<FindOrganizationsListResponse>> getAllOrganizations(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String keyword
    ) {
        OrganizationListQuery query = new OrganizationListQuery(page, size, keyword);
        OrganizationListResult result = organizationQueryUseCase.findOrganizations(query);
        FindOrganizationsListResponse response = organizationMapper.toListResponse(result);
        return ResponseEntity.ok(
                GlobalApiResponse.ok(OrganizationResponseCode.ORGANIZATION_LIST_FOUND, response)
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "조직 상세 조회 (관리자)", description = "관리자가 조직의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindOrganizationAdminDetailResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{organizationId}")
    public ResponseEntity<GlobalApiResponse<FindOrganizationAdminDetailResponse>> getOrganization(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long organizationId
    ) {
        OrganizationAdminDetailResult result = organizationQueryUseCase.findOrganizationAdminDetail(organizationId);
        boolean isAdmin = authUser.role().equals("ADMIN");
        FileUrlResult fileUrlResult = fileUrlUseCase.getUrl(result.businessLicenseFileId(), authUser.userId(), isAdmin);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        OrganizationResponseCode.ORGANIZATION_FOUND,
                        organizationMapper.toAdminDetailResponse(result, fileUrlResult.url(), fileUrlResult.originalName())
                )
        );
    }

    @Operation(summary = "조직 상세 조회 (사용자)", description = "사용자가 특정 조직의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindOrganizationDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{organizationId}/detail")
    public ResponseEntity<GlobalApiResponse<FindOrganizationDetailResponse>> findOrganizationDetail(
            @PathVariable Long organizationId
    ) {
        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        OrganizationResponseCode.ORGANIZATION_DETAIL_FOUND,
                        organizationMapper.toDetailResponse(organizationQueryUseCase.findOrganizationDetail(organizationId))
                )
        );
    }

    @PreAuthorize("hasAuthority('ORGANIZATION')")
    @Operation(summary = "내 조직 정보 조회", description = "조직 계정이 본인 조직 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindMyOrganizationResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/me")
    public ResponseEntity<GlobalApiResponse<FindMyOrganizationResponse>> getMyOrganization(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        MyOrganizationResult result = organizationQueryUseCase.findMyOrganization(authUser.userId());

        // 조직 계정은 파일 업로더(신청자)와 다른 계정이므로 소유권 체크 우회 허용
        // findMyOrganization()으로 본인 조직만 조회되고 fileId도 DB에서 가져오므로 우회 불가
        boolean isOrganization = authUser.role().equals("ORGANIZATION");
        FileUrlResult fileUrlResult = fileUrlUseCase.getUrl(
                result.businessLicenseFileId(), authUser.userId(), isOrganization);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        OrganizationResponseCode.ORGANIZATION_FOUND,
                        organizationMapper.toMyOrganizationResponse(result, fileUrlResult.url(), fileUrlResult.originalName())
                )
        );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER')")
    @Operation(summary = "조직 검색", description = "상호명 또는 대표자명으로 활성 조직을 검색합니다. 사용자/트레이너가 소속 신청 전 조직을 찾을 때 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = OrganizationSearchListResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/search")
    public ResponseEntity<GlobalApiResponse<OrganizationSearchListResponse>> searchOrganizations(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        OrganizationSearchListResult result = organizationQueryUseCase.searchOrganizations(keyword, page, size);
        return ResponseEntity.ok(
                GlobalApiResponse.ok(OrganizationResponseCode.ORGANIZATION_SEARCH_FOUND,
                        OrganizationSearchListResponse.from(result))
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
                new OrganizationUpdateCommand(
                        authUser.userId(),
                        request.facilityPhone(),
                        request.instagramUrl(),
                        request.blogUrl(),
                        request.websiteUrl()
                )
        );

        return ResponseEntity.ok(
                GlobalApiResponse.ok(OrganizationResponseCode.ORGANIZATION_UPDATED, null)
        );
    }

}
