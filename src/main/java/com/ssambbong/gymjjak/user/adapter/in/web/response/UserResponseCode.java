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
    ACCESS_TOKEN_REISSUED("TOKEN_REISSUED", "Access 토큰이 재발급되었습니다."),
    USER_PASSWORD_VERIFIED("USER_PASSWORD_VERIFIED", "비밀번호 확인에 성공했습니다."),
    USER_PROFILE_FOUND("USER_PROFILE_FOUND", "회원 프로필 조회에 성공했습니다."),
    USER_PROFILE_UPDATED("USER_PROFILE_UPDATED", "회원 프로필 수정에 성공했습니다."),
    USER_PROFILE_WITHDREW("USER_PROFILE_WITHDREW", "회원 탈퇴에 성공했습니다."),
    USER_STATUS_UPDATED("USER_STATUS_UPDATED", "회원 상태 변경에 성공했습니다."),
    PASSWORD_CHANGED("PASSWORD_CHANGED", "비밀번호 변경에 성공했습니다."),
    USER_FOUND("USER_FOUND", "유저 목록이 조회되었습니다."),
    TEMPORARY_PASSWORD_SENT("USER", "입력한 이메일로 임시 비밀번호가 발급되었습니다.");

    private final String code;
    private final String message;



}
