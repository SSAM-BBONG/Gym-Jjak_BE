package com.ssambbong.gymjjak.inbody.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.inbody.application.command.CreateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.command.UpdateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.query.GetInbodyListQuery;
import com.ssambbong.gymjjak.inbody.application.result.CreateInbodyResult;
import com.ssambbong.gymjjak.inbody.application.result.InbodyListResult;
import com.ssambbong.gymjjak.inbody.application.usecase.InbodyCommandUseCase;
import com.ssambbong.gymjjak.inbody.application.usecase.InbodyQueryUseCase;
import com.ssambbong.gymjjak.inbody.presentation.api.mapper.InbodyResponseMapper;
import com.ssambbong.gymjjak.inbody.presentation.api.request.CreateInbodyRequest;
import com.ssambbong.gymjjak.inbody.presentation.api.request.GetInbodyListRequest;
import com.ssambbong.gymjjak.inbody.presentation.api.request.UpdateInbodyRequest;
import com.ssambbong.gymjjak.inbody.presentation.api.response.CreateInbodyResponse;
import com.ssambbong.gymjjak.inbody.presentation.api.response.InbodyListResponse;
import com.ssambbong.gymjjak.inbody.presentation.api.response.InbodyResponseCode;
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

@Tag(name = "인바디", description = "인바디 기록 API")
@RestController
@RequestMapping("/api/inbody")
@RequiredArgsConstructor
public class InbodyController {

    private final InbodyCommandUseCase inbodyCommandUseCase;
    private final InbodyQueryUseCase  inbodyQueryUseCase;
    private final InbodyResponseMapper inbodyResponseMapper;

    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "인바디 기록 생성",
            description = "로그인한 사용자가 측정일, 키, 몸무게, 체지방률, 골격근량을 입력해 인바디 기록을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "인바디 기록 생성 성공",
                    content = @Content(schema = @Schema(implementation = CreateInbodyResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 인바디 생성 요청",
                    content = @Content(schema = @Schema())
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema())
            )
    })
    @PostMapping
    public ResponseEntity<GlobalApiResponse<CreateInbodyResponse>> createInbody(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreateInbodyRequest request
    ) {
        CreateInbodyCommand command = new CreateInbodyCommand(
                authUser.userId(),
                request.measuredDate(),
                request.height(),
                request.weight(),
                request.bodyFatPercentage(),
                request.skeletalMuscleMass()
        );

        CreateInbodyResult result = inbodyCommandUseCase.createInbody(command);

        return ResponseEntity.status(201).body(
                GlobalApiResponse.created(
                        InbodyResponseCode.INBODY_CREATED,
                        inbodyResponseMapper.toCreateResponse(result)
                )
        );
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "내 인바디 측정 기록 조회",
            description = "로그인한 사용자의 인바디 측정 기록을 최신 측정일 순으로 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "인바디 측정 기록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            )
    })
    @GetMapping
    public ResponseEntity<GlobalApiResponse<InbodyListResponse>> getInbodyList(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @ModelAttribute GetInbodyListRequest request
            ) {

        InbodyListResult result = inbodyQueryUseCase.getInbodyList(
                new GetInbodyListQuery(
                        authUser.userId(),
                        request.measuredDate(),
                        request.inbodyId()
                )
        );

        return ResponseEntity.status(200).body(
            GlobalApiResponse.ok(
                    InbodyResponseCode.INBODY_LIST_FETCHED,
                    inbodyResponseMapper.toInbodyListResponse(result)
            )
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{inbodyId}")
    public ResponseEntity<GlobalApiResponse<Long>> updateInbody(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long inbodyId,
            @Valid @RequestBody UpdateInbodyRequest request
    ) {
        inbodyCommandUseCase.updateInbody(
                new UpdateInbodyCommand(
                        authUser.userId(),
                        request.height(),
                        request.weight(),
                        request.bodyFatPercentage(),
                        request.skeletalMuscleMass()
                ),
                inbodyId
        );

        return ResponseEntity.status(201).body(
                GlobalApiResponse.created(
                        InbodyResponseCode.INBODY_UPDATE,
                        inbodyId
                )
        );
    }
}
