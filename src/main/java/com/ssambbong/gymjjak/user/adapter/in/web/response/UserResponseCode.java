package com.ssambbong.gymjjak.user.adapter.in.web.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserResponseCode implements ResponseCode {

    USER_REGISTERED("USER_REGISTERED", "회원가입이 완료되었습니다."),
    USER_LOGIN_SUCCESS("USER_LOGGEDIN", "로그인이 완료되었습니다."),
    USER_LOGOUT_SUCCESS("USER_LOGGEDOUT", "로그아웃이 완료되었습니다."),
    ACCESS_TOKEN_REISSUED("TOKEN_REISSUED", "Access 토큰이 재발급되었습니다.");

    private final String code;
    private final String message;



}
