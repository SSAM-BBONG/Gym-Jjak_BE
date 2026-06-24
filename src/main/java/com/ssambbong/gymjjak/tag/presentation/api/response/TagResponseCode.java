package com.ssambbong.gymjjak.tag.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagResponseCode implements ResponseCode {

    TAG_LIST_SUCCESS("TAG_200", "태그 목록 조회 성공"),
    TAG_CREATED("TAG_201", "태그 등록 성공"),
    TAG_UPDATED("TAG_200_UPDATE", "태그 수정 성공"),
    TAG_DELETED("TAG_200_DELETE", "태그 삭제 성공");

    private final String code;
    private final String message;
}
