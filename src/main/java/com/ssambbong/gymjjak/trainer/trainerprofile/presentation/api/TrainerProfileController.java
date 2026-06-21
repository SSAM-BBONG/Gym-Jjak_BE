package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase.TrainerProfileQueryUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response.MyTrainerProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(
        name = "트레이너 프로필",
        description = "트레이너 프로필 api (승인 후 등록되는 테이블)"
)
@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerProfileController {

//    private final TrainerProfileQueryUseCase trainerProfileQueryUseCase;

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
        return null;
    }
}
