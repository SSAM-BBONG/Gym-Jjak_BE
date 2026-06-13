package com.ssambbong.gymjjak.user.adapter.in.web;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.user.adapter.in.web.request.UpdateUserProfileRequest;
import com.ssambbong.gymjjak.user.adapter.in.web.request.PasswordVerificationRequest;
import com.ssambbong.gymjjak.user.adapter.in.web.response.UserProfileResponse;
import com.ssambbong.gymjjak.user.adapter.in.web.response.UserResponseCode;
import com.ssambbong.gymjjak.user.application.command.UpdateProfileCommand;
import com.ssambbong.gymjjak.user.application.port.in.UserCommandUseCase;
import com.ssambbong.gymjjak.user.application.result.UserProfileResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "회원을 관리하는 기능")
public class UserController {

    private final UserCommandUseCase userCommandUseCase;

    @PostMapping("/me/password-verification")
    @Operation(summary = "프로필 수정 전 비밀번호 확인", description = "프로필 수정 화면 진입 전 현재 비밀번호를 확인한다.")
    public ResponseEntity<GlobalApiResponse<Void>> verifyPassword(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody PasswordVerificationRequest request
    ) {
        userCommandUseCase.verifyPassword(authUser.userId(), request.password());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(UserResponseCode.USER_PASSWORD_VERIFIED)
        );
    }

    @GetMapping("/me")
    @Operation(summary = "프로필 정보 조회", description = "프로필 수정 화면에서 사용자 본인의 프로필 정보를 조회한다.")
    public ResponseEntity<GlobalApiResponse<UserProfileResponse>> findMyProfileInfo(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        UserProfileResult result = userCommandUseCase.findMyProfileInfo(authUser.userId());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(UserResponseCode.USER_PROFILE_FOUND,
                        new UserProfileResponse(
                                result.name(),
                                result.nickname(),
                                result.phone()
                        )
                ));
    }

    @PatchMapping("/me")
    @Operation(summary = "프로필 정보 수정", description = "프로필을 수정한다.")
    public ResponseEntity<GlobalApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        UpdateProfileCommand command = request.toCommand(authUser.userId());

        userCommandUseCase.updateProfile(command);

        return ResponseEntity.ok(
                GlobalApiResponse.ok(UserResponseCode.USER_PROFILE_UPDATED)
        );

    }


}
