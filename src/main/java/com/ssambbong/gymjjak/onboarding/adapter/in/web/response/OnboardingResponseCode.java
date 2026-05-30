package com.ssambbong.gymjjak.onboarding.adapter.in.web.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OnboardingResponseCode implements ResponseCode {

    ONBOARDING_CREATED("ONBOARDING_201_001", "온보딩 등록이 완료되었습니다."),
    ONBOARDING_FOUND("ONBOARDING_200_001", "온보딩이 조회되었습니다.");

    private final String code;
    private final String message;
}
