package com.ssambbong.gymjjak.inbody.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InbodyResponseCode implements ResponseCode {

    INBODY_CREATED(
            "INBODY_201_1",
            "인바디 기록이 등록되었습니다."
    );

    private final String code;
    private final String message;
}
