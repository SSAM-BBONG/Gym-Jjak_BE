package com.ssambbong.gymjjak.category.presectation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryResponseCode implements ResponseCode {

    CATEGORY_LIST_SUCCESS("CATEGORY_200", "카테고리 목록 조회 성공"),
    CATEGORY_CREATED("CATEGORY_201", "카테고리 등록 성공"),
    CATEGORY_UPDATED("CATEGORY_200", "카테고리 수정 성공"),
    CATEGORY_DELETED("CATEGORY_200", "카테고리 삭제 성공");

    private final String code;
    private final String message;
}
