package com.ssambbong.gymjjak.user.adapter.in.web;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.user.adapter.in.web.response.TokenResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
public class TokenController {

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
}
