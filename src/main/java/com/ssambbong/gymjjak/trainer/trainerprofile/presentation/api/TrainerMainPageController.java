package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerMainPageResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.usecase.TrainerMainPageQueryUseCase;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response.TrainerMainPageResponse;
import com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response.TrainerProfileResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/trainer")
@RequiredArgsConstructor
public class TrainerMainPageController {

    private final TrainerMainPageQueryUseCase trainerMainPageQueryUseCase;

    @GetMapping("/main")
    @PreAuthorize("hasAuthority('TRAINER')")
    // 로그인한 트레이너의 PT 운영 현황 대시보드를 조회합니다.
    public ResponseEntity<GlobalApiResponse<TrainerMainPageResponse>> findMainPage(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        TrainerMainPageResult result = trainerMainPageQueryUseCase.findMainPage(authUser.userId());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        TrainerProfileResponseCode.TRAINER_MAIN_PAGE_FOUND,
                        TrainerMainPageResponse.from(result)
                )
        );
    }
}
