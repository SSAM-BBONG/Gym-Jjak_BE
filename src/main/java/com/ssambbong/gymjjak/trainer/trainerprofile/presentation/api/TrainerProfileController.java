package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.UpdateProfileImageFileCommand;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.UpdateTrainerProfileCommand;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.MyTrainerProfileResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase.TrainerProfileCommandUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase.TrainerProfileQueryUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.request.TrainerProfileImageFileRequest;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.request.UpdateTrainerProfileRequest;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response.MyTrainerProfileResponse;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response.TrainerProfileResponseCode;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response.UpdateTrainerProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "트레이너 프로필",
        description = "트레이너 프로필 api (승인 후 등록되는 테이블)"
)
@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerProfileController {

    private final TrainerProfileQueryUseCase trainerProfileQueryUseCase;
    private final TrainerProfileCommandUseCase  trainerProfileCommandUseCase;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(
            summary = "내 트레이너 프로필 상세 조회",
            description = "로그인한 트레이너가 자신의 프로필, 자격증, 수상 경력을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "내 트레이너 프로필 상세 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "트레이너 권한 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "트레이너 프로필을 찾을 수 없음"
            )
    })
    public ResponseEntity<GlobalApiResponse<MyTrainerProfileResponse>
            > getMyTrainerProfile(
            @AuthenticationPrincipal AuthUser authUser
            ) {
        MyTrainerProfileResult result =
                trainerProfileQueryUseCase.getMyTrainerProfile(
                        authUser.userId()
                );

        return ResponseEntity.status(200).body(
                GlobalApiResponse.ok(
                        TrainerProfileResponseCode.MY_TRAINER_PROFILE_FOUND,
                        MyTrainerProfileResponse.from(result)
                )
        );
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAuthority('TRAINER')")
    @Operation(
            summary = "내 트레이너 프로필 수정",
            description = """
                로그인한 트레이너의 프로필 이미지, 추가 자격증,
                수상 및 대회 경력, 자기소개를 수정한다.
                이름과 필수 자격증은 수정 불가.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "내 트레이너 프로필 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 수정 요청"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "트레이너 권한 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "트레이너 프로필을 찾을 수 없음"
            )
    })
    public ResponseEntity<
            GlobalApiResponse<UpdateTrainerProfileResponse>> updateMyTrainerProfile(
            @AuthenticationPrincipal  AuthUser authUser,
            @RequestBody @Valid UpdateTrainerProfileRequest request
    ) {
        Long trainerProfileId = trainerProfileCommandUseCase.updateMyTrainerProfile(
                new UpdateTrainerProfileCommand(
                        authUser.userId(),
                        request.profileImageAction(),
                        toCommand(request.profileImageFile()),
                        request.additionalCertifications(),
                        request.awardHistories(),
                        request.introduction()
                )
        );

        return ResponseEntity.status(201).body(
                GlobalApiResponse.created(
                        TrainerProfileResponseCode.TRAINER_PROFILE_UPDATED,
                        new UpdateTrainerProfileResponse(trainerProfileId)
                )
        );
    }

    private UpdateProfileImageFileCommand toCommand(
            TrainerProfileImageFileRequest request
    ) {
        if (request == null) {
            return null;
        }
        return new UpdateProfileImageFileCommand(
                request.fileKey(),
                request.originalName(),
                request.contentType(),
                request.fileSize()
        );
    }
}
