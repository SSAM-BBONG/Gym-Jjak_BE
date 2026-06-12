package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.command.CreateTrainerApplicationCommand;
import com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase.TrainerApplicationCommandUseCase;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request.CreateTrainerApplicationRequest;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response.CreateTrainerApplicationResponse;
import com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response.TrainerApplicationResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/trainer-applications")
@RequiredArgsConstructor
public class TrainerApplicationController {

    private final TrainerApplicationCommandUseCase trainerApplicationCommandUseCase;

    @Operation(
            summary = "트레이너 신청",
            description = "사용자가 트레이너 신청을 요청합니다. 자격증 파일은 OCR 검증에 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "트레이너 신청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 처리 중인 트레이너 신청 존재"),
            @ApiResponse(responseCode = "502", description = "OCR 외부 API 요청 실패")
    })
    @PostMapping
    public ResponseEntity<GlobalApiResponse<CreateTrainerApplicationResponse>> createTrainerApplication(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreateTrainerApplicationRequest request
            ) {
        Long trainerApplicationId = trainerApplicationCommandUseCase.createTrainerApplication(
                new CreateTrainerApplicationCommand(
                        authUser.userId(),
                        request.profileImageFileId(),
                        request.certificateFileId(),
                        request.qualifications(),
                        request.awardHistories(),
                        request.introduction()
                )
        );

        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(
                        TrainerApplicationResponseCode.TRAINER_APPLICATION_CREATED,
                        new CreateTrainerApplicationResponse(trainerApplicationId)
                ));
    }
}
