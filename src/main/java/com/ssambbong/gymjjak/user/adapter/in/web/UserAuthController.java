package com.ssambbong.gymjjak.user.adapter.in.web;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.user.adapter.in.web.request.LoginRequest;
import com.ssambbong.gymjjak.user.adapter.in.web.request.ReissueTokenRequest;
import com.ssambbong.gymjjak.user.adapter.in.web.response.LoginResponse;
import com.ssambbong.gymjjak.user.adapter.in.web.response.ReissueTokenResponse;
import com.ssambbong.gymjjak.user.application.command.LoginCommand;
import com.ssambbong.gymjjak.user.application.command.RegisterUserCommand;
import com.ssambbong.gymjjak.user.application.command.ReissueTokenCommand;
import com.ssambbong.gymjjak.user.application.port.in.UserCommandUseCase;
import com.ssambbong.gymjjak.user.adapter.in.web.request.SignupRequest;
import com.ssambbong.gymjjak.user.adapter.in.web.response.UserResponseCode;
import com.ssambbong.gymjjak.user.application.result.LoginResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "User", description = "회원가입, 로그인을 처리하는 API")
public class UserAuthController {

    private final UserCommandUseCase userCommandUseCase;

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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalApiResponse.ok(
                        UserResponseCode.USER_LOGIN_SUCCESS,
                        new LoginResponse(
                                result.accessToken(),
                                result.refreshToken()
                        )
                ));
    }

}
