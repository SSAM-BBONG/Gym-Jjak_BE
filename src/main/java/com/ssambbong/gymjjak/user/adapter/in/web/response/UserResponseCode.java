package com.ssambbong.gymjjak.user.adapter.in.web.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserResponseCode implements ResponseCode {

    USER_REGISTERED("USER_REGISTERED", "회원가입이 완료되었습니다.");

    private final String code;
    private final String message;



}
