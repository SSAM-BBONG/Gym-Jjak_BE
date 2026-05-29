package com.ssambbong.gymjjak.user.adapter.in.web.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenResponseCode implements ResponseCode {

    ACCESS_TOKEN_VALID("USER_200_004", "유효한 AccessToken입니다.");

    private final String code;
    private final String message;
}
