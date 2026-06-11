package com.ssambbong.gymjjak.user.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "USER_409_001", "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "USER_409_002", "이미 사용 중인 닉네임입니다."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "USER_409_003", "이미 사용 중인 전화번호입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "USER_401_001", "아이디 또는 비밀번호가 올바르지 않습니다."),
    LOGIN_RESTRICTED(HttpStatus.FORBIDDEN, "USER_403_001", "로그인이 제한된 계정입니다."),
    USER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "USER_403_002", "정상 상태의 회원만 사용할 수 있습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "USER_401_002", "유효하지 않은 Refresh Token입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER_401_003", "저장된 Refresh Token이 없습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "USER_401_004", "Refresh Token 정보가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_001", "사용자를 찾을 수 없습니다."),
    USER_WITHDRAWN(HttpStatus.BAD_REQUEST, "USER_400_002", "탈퇴한 회원입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_400_001", "비밀번호는 8자 이상 16자 이하이며, 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."),
    ONBOARDING_ALREADY_COMPLETED(HttpStatus.CONFLICT, "USER_409_004", "이미 온보딩이 완료된 사용자입니다."),
    USER_ALREADY_WITHDRAWN(HttpStatus.CONFLICT, "USER_409_005", "이미 탈퇴한 회원입니다."),
    USER_ALREADY_SEVEN_DAYS_SUSPENDED(HttpStatus.CONFLICT, "USER_409_006", "이미 7일 정지된 회원입니다."),
    USER_ALREADY_PERMANENTLY_SUSPENDED(HttpStatus.CONFLICT, "USER_409_007", "이미 영구 정지된 회원입니다."),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "USER_401_005", "비밀번호가 일치하지 않습니다."),
    USERNAME_REQUIRED(HttpStatus.BAD_REQUEST, "USER_400_003", "아이디는 필수입니다."),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "USER_400_004", "비밀번호는 필수입니다."),
    NAME_REQUIRED(HttpStatus.BAD_REQUEST, "USER_400_005", "이름은 필수입니다."),
    NICKNAME_REQUIRED(HttpStatus.BAD_REQUEST, "USER_400_006", "닉네임은 필수입니다."),
    PHONE_REQUIRED(HttpStatus.BAD_REQUEST, "USER_400_007", "전화번호는 필수입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
