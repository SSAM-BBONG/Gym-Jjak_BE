package com.ssambbong.gymjjak.user.adapter.in.web;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.user.adapter.in.web.request.CompleteSocialSignupRequest;
import com.ssambbong.gymjjak.user.adapter.in.web.request.IssueTemporaryPasswordRequest;
import com.ssambbong.gymjjak.user.adapter.in.web.request.LoginRequest;
import com.ssambbong.gymjjak.user.adapter.in.web.response.LoginResponse;
import com.ssambbong.gymjjak.user.application.command.CompleteSocialSignupCommand;
import com.ssambbong.gymjjak.user.application.command.LoginCommand;
import com.ssambbong.gymjjak.user.application.command.LogoutCommand;
import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
import com.ssambbong.gymjjak.user.application.port.in.MailUseCase;
import com.ssambbong.gymjjak.user.application.port.in.UserCommandUseCase;
import com.ssambbong.gymjjak.user.adapter.in.web.request.SignupRequest;
import com.ssambbong.gymjjak.user.adapter.in.web.response.UserResponseCode;
import com.ssambbong.gymjjak.user.application.result.LoginResult;
import com.ssambbong.gymjjak.user.domain.exception.UserErrorCode;
import com.ssambbong.gymjjak.user.domain.exception.UserException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "User", description = "회원가입, 로그인을 처리하는 API")
public class UserAuthController {

    private final UserCommandUseCase userCommandUseCase;
    private final MailUseCase mailUseCase;

    @PostMapping("/signup")
    @Operation( summary = "일반 회원 계정 생성", description = "회원이 회원가입을 하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {

        userCommandUseCase.registerUser(new RegisterUserCommand(
                request.username(),
                request.password(),
                request.name(),
                request.nickname(),
                request.phone()
        ));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalApiResponse.created(
                        UserResponseCode.USER_REGISTERED
                ));

    }

    @PostMapping("/login")
    @Operation( summary = "로그인", description = "로그인을 하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResult result = userCommandUseCase.login(new LoginCommand(
                request.username(),
                request.password()
        ));

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", result.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(GlobalApiResponse.ok(
                        UserResponseCode.USER_LOGIN_SUCCESS,
                        new LoginResponse(
                                result.accessToken(),
                                result.role(),
                                result.onboardingCompleted()

                        )
                ));
    }

    @PostMapping("/logout")
    @Operation( summary = "로그아웃", description = "로그아웃을 하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<Void>> logout( @AuthenticationPrincipal AuthUser authUser,
                                                           HttpServletResponse response) {

        if (authUser == null) {
            throw new UserException(UserErrorCode.AUTHORIZATION_FAILED);
        }

        userCommandUseCase.logout(new LogoutCommand(authUser.userId()));

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalApiResponse.ok(
                        UserResponseCode.USER_LOGOUT_SUCCESS
                ));

    }

    @PostMapping("/password")
    @Operation(summary = "임시 비밀번호 발급", description = "임시 비밀번호를 발급하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<Void>> issueTemporaryPassword(@Valid @RequestBody IssueTemporaryPasswordRequest request) {
        mailUseCase.issueTemporaryPassword(request.toCommand());

        return ResponseEntity.ok(
                GlobalApiResponse.ok(UserResponseCode.TEMPORARY_PASSWORD_SENT)
        );
    }

    @PatchMapping("/social/complete")
    @Operation(summary = "소셜 회원 추가 정보 입력", description = "소셜 로그인 회원의 닉네임과 전화번호를 입력한다.")
    public ResponseEntity<GlobalApiResponse<Void>> completeSocialSignup(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CompleteSocialSignupRequest request
    ) {

        if (authUser == null) {
            throw new UserException(UserErrorCode.AUTHORIZATION_FAILED);
        }

        userCommandUseCase.completeSocialSignup(
                new CompleteSocialSignupCommand(
                        authUser.userId(),
                        request.nickname(),
                        request.phone()
                )
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalApiResponse.ok(
                        UserResponseCode.SOCIAL_SIGNUP_COMPLETED
                ));
    }

}
