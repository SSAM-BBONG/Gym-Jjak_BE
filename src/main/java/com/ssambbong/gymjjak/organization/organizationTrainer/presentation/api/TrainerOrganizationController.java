package com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.usecase.OrganizationTrainerQueryUseCase;
import com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.response.MyOrganizationResponse;
import com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.response.OrganizationTrainerResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "OrganizationTrainer", description = "조직 소속 트레이너 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations/trainer")
public class TrainerOrganizationController {

    private final OrganizationTrainerQueryUseCase organizationTrainerQueryUseCase;

    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(summary = "내 소속 조직 목록 조회", description = "트레이너가 본인이 소속된 조직 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MyOrganizationResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "트레이너 권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "트레이너 프로필 없음 또는 소속 조직 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/my-organizations")
    public ResponseEntity<GlobalApiResponse<List<MyOrganizationResponse>>> findMyOrganizations(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        List<MyOrganizationResponse> response = organizationTrainerQueryUseCase
                .findMyOrganizations(authUser.userId())
                .stream()
                .map(r -> new MyOrganizationResponse(r.organizationId(), r.businessName(), r.roadAddress()))
                .toList();
        return ResponseEntity.ok(GlobalApiResponse.ok(
                OrganizationTrainerResponseCode.MY_ORGANIZATIONS_FETCHED, response));
    }
}
