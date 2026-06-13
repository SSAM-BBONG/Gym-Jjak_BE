package com.ssambbong.gymjjak.user.adapter.in.web;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.user.adapter.in.web.request.PasswordVerificationRequest;
import com.ssambbong.gymjjak.user.adapter.in.web.response.UserResponseCode;
import com.ssambbong.gymjjak.user.application.port.in.UserCommandUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
