package com.ssambbong.gymjjak.onboarding.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OnboardingErrorCode implements ErrorCode {

    ONBOARDING_ALREADY_COMPLETED(HttpStatus.CONFLICT, "USER_409_004", "이미 온보딩이 완료된 사용자입니다."),
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "REGION_404", "지역 정보를 찾을 수 없습니다."),
    ONBOARDING_NOT_FOUND(HttpStatus.NOT_FOUND, "ONBOARDING_404", "온보딩을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
