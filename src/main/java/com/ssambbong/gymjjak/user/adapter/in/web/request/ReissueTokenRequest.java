package com.ssambbong.gymjjak.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "AccessToken 재발급 요청")
public record ReissueTokenRequest(

        @Schema(
                description = "로그인 시 발급받은 RefreshToken",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwidXNlcm5hbWUiOiJ0ZXN0MTIzNEB0ZXN0LmNvbSIsImlhdCI6MTc3OTk1NzM2MiwiZXhwIjoxNzgxMTY2OTYyfQ.B0sBnehAe31aYgG8VaaRI09SmU2AbiXFq88Ylj_oPT0"
        )
        @NotBlank(message = "refreshToken은 필수입니다.")
        String refreshToken

) {
}
