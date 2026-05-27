package com.ssambbong.gymjjak.category.presectation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryResponseCode implements ResponseCode {

    CATEGORY_LIST_SUCCESS("CATEGORY_001", "카테고리 목록 조회 성공"),
    CATEGORY_CREATED("CATEGORY_002", "카테고리 등록 성공"),
    CATEGORY_UPDATED("CATEGORY_003", "카테고리 수정 성공"),
    CATEGORY_DELETED("CATEGORY_004", "카테고리 삭제 성공");

    private final String code;
    private final String message;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
