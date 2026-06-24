package com.ssambbong.gymjjak.calendar.adapter.in.web.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CalendarResponseCode implements ResponseCode {

    DIARY_CREATED("DIARY_CREATED", "일지가 작성되었습니다."),
    DIARY_UPDATED("DIARY_UPDATED", "일지가 수정되었습니다."),
    DIARY_DELETED("DIARY_DELETED", "일지가 삭제되었습니다."),
    CALENDAR_FETCHED("CALENDAR_FETCHED", "캘린더 조회가 완료되었습니다."),
    CALENDAR_DAY_FETCHED("CALENDAR_DAY_FETCHED", "날짜별 캘린더 조회가 완료되었습니다.");

    private final String code;
    private final String message;
}
