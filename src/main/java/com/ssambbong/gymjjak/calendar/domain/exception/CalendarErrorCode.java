package com.ssambbong.gymjjak.calendar.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarErrorCode implements ErrorCode {

    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "USER_ID_REQUIRED", "유저 ID는 필수입니다."),
    CATEGORY_ID_REQUIRED(HttpStatus.BAD_REQUEST, "CATEGORY_ID_REQUIRED", "카테고리는 필수입니다."),
    DIARY_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "DIARY_DATE_REQUIRED", "일지 날짜는 필수입니다."),
    DIARY_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "DIARY_TITLE_REQUIRED", "일지 제목은 필수입니다."),
    DIARY_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "DIARY_TITLE_TOO_LONG", "일지 제목은 100자 이하로 입력해야 합니다."),
    DIARY_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "DIARY_CONTENT_REQUIRED", "일지 내용은 필수입니다."),
    DIARY_ALREADY_EXISTS(HttpStatus.CONFLICT, "DIARY_ALREADY_EXISTS", "해당 날짜에 이미 작성된 일지가 있습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_404", "존재하지 않는 카테고리입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
