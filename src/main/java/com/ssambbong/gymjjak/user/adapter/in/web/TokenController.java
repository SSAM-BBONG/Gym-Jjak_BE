package com.ssambbong.gymjjak.user.adapter.in.web;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.user.adapter.in.web.response.ReissueTokenResponse;
import com.ssambbong.gymjjak.user.adapter.in.web.response.TokenResponseCode;
import com.ssambbong.gymjjak.user.adapter.in.web.response.UserResponseCode;
import com.ssambbong.gymjjak.user.application.command.ReissueTokenCommand;
import com.ssambbong.gymjjak.user.application.port.in.TokenCommandUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@Tag(name = "Token", description = "토큰 검증을 처리하는 API")
public class TokenController {

    private final TokenCommandUsecase tokenCommandUsecase;

    public TokenController(TokenCommandUsecase tokenCommandUsecase) {
        this.tokenCommandUsecase = tokenCommandUsecase;
    }

    @GetMapping("/validate")
    @Operation(
            summary = "AccessToken 유효성 확인",
            description = "AccessToken이 유효한지 확인한다. 유효하면 200 OK를 반환하고, 만료되었거나 유효하지 않으면 401을 반환한다."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<GlobalApiResponse<Void>> validateAccessToken() {
        return ResponseEntity.ok(
                GlobalApiResponse.ok(TokenResponseCode.ACCESS_TOKEN_VALID)
        );
    }

    @PostMapping("/reissue")
    @Operation(summary = "AccessToken 재발급", description = "RefreshToken을 검증한 뒤 새로운 AccessToken을 발급한다.")
    public ResponseEntity<GlobalApiResponse<ReissueTokenResponse>> reissueAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        String accessToken = tokenCommandUsecase.reissueAccessToken(
                new ReissueTokenCommand(refreshToken)
        );

        return ResponseEntity.ok(
                GlobalApiResponse.ok(
                        UserResponseCode.ACCESS_TOKEN_REISSUED,
                        new ReissueTokenResponse(accessToken)
                )
        );
    }
}
