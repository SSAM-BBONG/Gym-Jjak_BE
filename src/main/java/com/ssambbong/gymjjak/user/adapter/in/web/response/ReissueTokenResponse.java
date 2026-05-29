package com.ssambbong.gymjjak.user.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AccessToken 재발급 응답")
public record ReissueTokenResponse(

        @Schema(
                description = "새로 발급된 AccessToken",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwidXNlcm5hbWUiOiJ0ZXN0MTIzNEB0ZXN0LmNvbSIsImlhdCI6MTc3OTk1NzM2MiwiZXhwIjoxNzgxMTY2OTYyfQ.B0sBnehAe31aYgG8VaaRI09SmU2AbiXFq88Ylj_oPT0"
        )
        String accessToken

) {
}
