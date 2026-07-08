package com.ssambbong.gymjjak.part.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartResponseCode implements ResponseCode {

    PART_LIST_SUCCESS("PART_200", "부위 목록 조회 성공"),
    PART_CREATED("PART_201", "부위 등록 성공"),
    PART_UPDATED("PART_200_UPDATE", "부위 수정 성공"),
    PART_DELETED("PART_200_DELETE", "부위 삭제 성공");

    private final String code;
    private final String message;
}
