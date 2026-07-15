package com.ssambbong.gymjjak.inbody.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InbodyResponseCode implements ResponseCode {

    INBODY_LIST_FETCHED(
            "INBODY_200_1",
            "인바디 측정 기록을 조회했습니다."
    ),
    INBODY_CREATED(
            "INBODY_201_1",
            "인바디 기록이 등록되었습니다."
    ),
    INBODY_UPDATE(
            "INBODY_201_2",
            "인바디 수정이 완료되었습니다."
    );

    private final String code;
    private final String message;
}
