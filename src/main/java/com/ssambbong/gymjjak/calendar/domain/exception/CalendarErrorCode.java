package com.ssambbong.gymjjak.calendar.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarErrorCode implements ErrorCode {

    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "CALENDAR_400_001", "유저를 찾을 수 없습니다."),
    CATEGORY_ID_REQUIRED(HttpStatus.BAD_REQUEST, "CALENDAR_400_002", "카테고리는 필수입니다."),
    DIARY_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "CALENDAR_400_003", "일지 날짜는 필수입니다."),
    DIARY_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "CALENDAR_400_004", "일지 제목은 필수입니다."),
    DIARY_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "CALENDAR_400_005", "일지 제목은 100자 이하로 입력해야 합니다."),
    DIARY_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "CALENDAR_400_006", "일지 내용은 필수입니다."),
    DIARY_ALREADY_EXISTS(HttpStatus.CONFLICT, "CALENDAR_409_001", "해당 날짜에 이미 작성된 일지가 있습니다."),
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND,"DIARY_404_001", "일지를 찾을 수 없습니다."),
    DATE_REQUIRED(HttpStatus.BAD_REQUEST, "CALENDAR_400_002", "날짜는 필수입니다."),
    YEAR_REQUIRED(HttpStatus.BAD_REQUEST, "CALENDAR_400_003", "연도는 필수입니다."),
    MONTH_REQUIRED(HttpStatus.BAD_REQUEST, "CALENDAR_400_004", "월은 필수입니다."),
    INVALID_MONTH(HttpStatus.BAD_REQUEST, "CALENDAR_400_005", "월은 1부터 12 사이여야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
