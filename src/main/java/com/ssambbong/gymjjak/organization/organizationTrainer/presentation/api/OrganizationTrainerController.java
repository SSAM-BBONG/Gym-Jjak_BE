package com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.usecase.OrganizationTrainerCommandUseCase;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.usecase.OrganizationTrainerQueryUseCase;
import com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.mapper.OrganizationTrainerMapper;
import com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.request.AddOrganizationTrainerRequest;
import com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.response.AddOrganizationTrainerResponse;
import com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.response.FindOrganizationTrainersResponse;
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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OrganizationTrainer", description = "조직 소속 트레이너 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations/me/trainers")
public class OrganizationTrainerController {

    private final OrganizationTrainerQueryUseCase organizationTrainerQueryUseCase;
    private final OrganizationTrainerCommandUseCase organizationTrainerCommandUseCase;
    private final OrganizationTrainerMapper organizationTrainerMapper;

    @PreAuthorize("hasAuthority('ORGANIZATION')")
    @Operation(summary = "내 조직 소속 트레이너 목록 조회", description = "조직 계정이 본인 조직 소속 트레이너 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FindOrganizationTrainersResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public ResponseEntity<GlobalApiResponse<FindOrganizationTrainersResponse>> getOrganizationTrainers(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        FindOrganizationTrainersResponse response = new FindOrganizationTrainersResponse(
                organizationTrainerMapper.toResponseList(
                        organizationTrainerQueryUseCase.findMyOrganizationTrainers(authUser.userId())
                )
        );

        return ResponseEntity.ok(
                GlobalApiResponse.ok(OrganizationTrainerResponseCode.ORGANIZATION_TRAINER_LIST_FOUND, response)
        );
    }

    @PreAuthorize("hasAuthority('ORGANIZATION')")
    @Operation(summary = "소속 트레이너 추가", description = "조직 계정이 본인 조직에 트레이너를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "추가 성공",
                    content = @Content(schema = @Schema(implementation = AddOrganizationTrainerResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "이미 소속된 트레이너",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping
    public ResponseEntity<GlobalApiResponse<AddOrganizationTrainerResponse>> addOrganizationTrainer(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid AddOrganizationTrainerRequest request
    ) {
        Long organizationTrainerId = organizationTrainerCommandUseCase.addTrainer(
                organizationTrainerMapper.toCommand(request, authUser.userId()));

        return ResponseEntity.status(201).body(
                GlobalApiResponse.created(
                        OrganizationTrainerResponseCode.ORGANIZATION_TRAINER_ADDED,
                        new AddOrganizationTrainerResponse(organizationTrainerId))
        );
    }

    @PreAuthorize("hasAuthority('ORGANIZATION')")
    @Operation(summary = "소속 트레이너 삭제", description = "조직 계정이 본인 조직 소속 트레이너를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "조직 또는 소속 트레이너를 찾을 수 없음",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{organizationTrainerId}")
    public ResponseEntity<GlobalApiResponse<Void>> removeOrganizationTrainer(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long organizationTrainerId
    ) {
        organizationTrainerCommandUseCase.removeTrainer(authUser.userId(), organizationTrainerId);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(OrganizationTrainerResponseCode.ORGANIZATION_TRAINER_REMOVED, null)
        );
    }
}
