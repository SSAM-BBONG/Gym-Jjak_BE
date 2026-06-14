package com.ssambbong.gymjjak.onboarding.adapter.in.web;


import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.onboarding.adapter.in.web.request.CompleteOnboardingRequest;
import com.ssambbong.gymjjak.onboarding.adapter.in.web.request.UpdateOnboardingRequest;
import com.ssambbong.gymjjak.onboarding.adapter.in.web.response.MyOnboardingResponse;
import com.ssambbong.gymjjak.onboarding.adapter.in.web.response.OnboardingResponseCode;
import com.ssambbong.gymjjak.onboarding.application.command.RegionCommand;
import com.ssambbong.gymjjak.onboarding.application.command.RegisterOnboardingCommand;
import com.ssambbong.gymjjak.onboarding.application.command.UpdateOnboardingCommand;
import com.ssambbong.gymjjak.onboarding.application.port.in.OnboardingUsecase;
import com.ssambbong.gymjjak.onboarding.application.result.MyOnboardingResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/onboarding")
@Tag(name = "Onboarding", description = "온보딩 관련 API")
public class OnboardingController {

    private final OnboardingUsecase onboardingUsecase;

    @PostMapping("/me")
    @Operation(summary = "온보딩 등록", description = "로그인한 사용자의 온보딩 설문 정보와 선호 지역을 등록한다."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<GlobalApiResponse<Void>> registerOnboarding(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CompleteOnboardingRequest request
    ) {
        log.info("[onboarding] 온보딩 등록 요청. userId={}, username={}",
                authUser.userId(),
                authUser.username());

        onboardingUsecase.register(new RegisterOnboardingCommand(
                authUser.userId(),
                request.exerciseGoal(),
                request.exercisePeriod(),
                request.exerciseFrequency(),
                request.preferredExercise(),
                request.height(),
                request.weight(),
                new RegionCommand(
                        request.region().sido(),
                        request.region().sigungu(),
                        request.region().eupmyeondong(),
                        request.region().fullName(),
                        request.region().latitude(),
                        request.region().longitude()
                )
        ));

        log.info("[onboarding] 온보딩 등록 요청 처리 완료. userId={}", authUser.userId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GlobalApiResponse.created(
                        OnboardingResponseCode.ONBOARDING_CREATED
                ));
    }

    @GetMapping("/me")
    @Operation(summary = "온보딩 조회", description = "로그인한 사용자의 온보딩 설문 정보와 선호 지역을 조회한다."
    )
    public ResponseEntity<GlobalApiResponse<MyOnboardingResponse>> getMyOnboarding(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        MyOnboardingResult result = onboardingUsecase.getMyOnboarding(authUser.userId());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        OnboardingResponseCode.ONBOARDING_FOUND,
                        MyOnboardingResponse.from(result)
                )
        );
    }

    @PutMapping("/me")
    @Operation(summary = "온보딩 수정", description = "로그인한 사용자의 온보딩 설문 정보와 선호 지역을 수정한다.")
    public ResponseEntity<GlobalApiResponse<Void>> updateOnboarding(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UpdateOnboardingRequest request
    ) {
        onboardingUsecase.updateOnboarding(new UpdateOnboardingCommand(
                authUser.userId(),
                request.exerciseGoal(),
                request.exercisePeriod(),
                request.exerciseFrequency(),
                request.preferredExercise(),
                request.height(),
                request.weight(),
                new RegionCommand(
                        request.region().sido(),
                        request.region().sigungu(),
                        request.region().eupmyeondong(),
                        request.region().fullName(),
                        request.region().latitude(),
                        request.region().longitude()
                )
        ));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalApiResponse.ok(
                        OnboardingResponseCode.ONBOARDING_UPDATED
                ));

    }
}