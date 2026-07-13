package com.ssambbong.gymjjak.inbody.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.inbody.application.command.CreateInbodyCommand;
import com.ssambbong.gymjjak.inbody.application.result.CreateInbodyResult;
import com.ssambbong.gymjjak.inbody.application.usecase.InbodyCommandUseCase;
import com.ssambbong.gymjjak.inbody.presentation.api.mapper.InbodyResponseMapper;
import com.ssambbong.gymjjak.inbody.presentation.api.request.CreateInbodyRequest;
import com.ssambbong.gymjjak.inbody.presentation.api.response.CreateInbodyResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인바디", description = "인바디 기록 API")
@RestController
@RequestMapping("/api/inbody")
@RequiredArgsConstructor
public class InbodyController {

    private final InbodyCommandUseCase inbodyCommandUseCase;
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
}
